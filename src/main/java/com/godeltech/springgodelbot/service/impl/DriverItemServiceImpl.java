package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.exception.ResourceNotFoundException;
import com.godeltech.springgodelbot.mapper.DriverItemMapper;
import com.godeltech.springgodelbot.model.entity.*;
import com.godeltech.springgodelbot.model.entity.enums.Activity;
import com.godeltech.springgodelbot.model.repository.DriverItemRepository;
import com.godeltech.springgodelbot.service.ActivityTypeService;
import com.godeltech.springgodelbot.service.CityService;
import com.godeltech.springgodelbot.service.DriverItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.checkRouteForParcel;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.checkRouteForPassenger;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DriverItemServiceImpl implements DriverItemService {

    private final DriverItemRepository driverItemRepository;
    private final DriverItemMapper driverItemMapper;
    private final CityService cityService;
    private final ActivityTypeService activityTypeService;

    @Override
    @Transactional
    public DriverItem save(DriverRequest driverRequest, User user, Message message) {
        log.info("Save supplier by: {}", driverRequest);
        List<City> cities = cityService.findCitiesByName(driverRequest.getCities(), message, user);
        List<ActivityType> suitableActivities = activityTypeService
                .getActivities(driverRequest.getSuitableActivities(), message, user);
        DriverItem offer = driverItemMapper.mapToOffer(driverRequest, user, cities, suitableActivities);
        return driverItemRepository.save(offer);
    }


    @Override
    public List<DriverItem> findDriversByFirstDateBeforeAndSecondDateAfterAndRoutes
            (LocalDate secondDate, LocalDate firstDate, List<String> cities, Activity activity) {
        log.info("Find drivers by first date :{} and second date:{} with cities:{}", firstDate, secondDate, cities);
        return secondDate == null ?
                driverItemRepository.findByFirstDateAndCitiesAndActivity(firstDate, cities, activity.name()).stream()
                        .peek(offer -> offer.setCities(cityService.findCitiesForDriverItemByOfferId(offer.getId())))
                        .filter(offer -> checkRoute(cities, offer, activity))
                        .collect(Collectors.toList()) :
                driverItemRepository.findByDatesAndCitiesAndActivity(secondDate, firstDate, cities, activity.name()).stream()
                        .peek(offer -> offer.setCities(cityService.findCitiesForDriverItemByOfferId(offer.getId())))
                        .filter(offer -> checkRoute(cities, offer, activity))
                        .collect(Collectors.toList());
    }


    @Override
    public List<ChangeOfferRequest> findByUserEntityId(Long id, Message message, User user) {
        log.info("Find driver items by id:{}", id);
        return driverItemRepository.findByUserEntityId(id).stream()
                .peek(offer -> offer.setCities(cityService.findCitiesForDriverItemByOfferId(offer.getId())))
                .map(driverItemMapper::mapToChangeOfferRequest)
                .collect(Collectors.toList());
    }

    @Override
    public ChangeOfferRequest getById(Long offerId, Message message, User user) {
        log.info("Find offer by id : {}", offerId);
        return driverItemRepository.findById(offerId)
                .map(offer -> {
                    List<City> cities = cityService.findCitiesForDriverItemByOfferId(offerId);
                    offer.setCities(cities);
                    return driverItemMapper.mapToChangeOfferRequest(offer);
                })
                .orElseThrow(() -> new ResourceNotFoundException(DriverItem.class, "id", offerId, message, user));
    }

    @Override
    @Transactional
    public void deleteById(Long offerId, Message message, User user) {
        log.info("Delete driver by id : {}", offerId);
        DriverItem driverItem = getTripOfferById(offerId, message, user);
        driverItemRepository.delete(driverItem);
    }

    private DriverItem getTripOfferById(Long offerId, Message message, User user) {
        log.info("Get offer by id: {}", offerId);
        DriverItem driverItem = driverItemRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException(DriverItem.class, "id", offerId, message, user));
        driverItem.setCities(cityService.findCitiesForDriverItemByOfferId(offerId));
        return driverItem;
    }

    @Override
    @Transactional
    public void updateCitiesOfDriverItem(ChangeOfferRequest changeOfferRequest, Message message, User user) {
        log.info("Update cities of tripOffer with id : {} and cities : {} ", changeOfferRequest.getOfferId(),
                changeOfferRequest.getCities());
        DriverItem driverItem = getTripOfferById(changeOfferRequest.getOfferId(), message, user);
        List<City> cities = cityService.findCitiesByName(changeOfferRequest.getCities(), message, user);
        driverItem.setCities(cities);
        driverItemRepository.save(driverItem);
    }

    @Override
    @Transactional
    public void updateDatesOfTripOffer(ChangeOfferRequest changeOfferRequest, Message message, User user) {
        log.info("Update date of driver with first date : {} , and second date : {} ", changeOfferRequest.getFirstDate()
                , changeOfferRequest.getSecondDate());
        DriverItem driverItem = getTripOfferById(changeOfferRequest.getOfferId(), message, user);
        driverItem.setFirstDate(changeOfferRequest.getFirstDate());
        driverItem.setSecondDate(changeOfferRequest.getSecondDate() == null ?
                null :
                changeOfferRequest.getSecondDate());
        driverItemRepository.save(driverItem);
    }

    @Override
    @Transactional
    public void updateDescriptionOfTripOffer(ChangeOfferRequest changeOfferRequest, Message message, User user) {
        log.info("Update description of driver with id : {}", changeOfferRequest.getOfferId());
        DriverItem driverItem = getTripOfferById(changeOfferRequest.getOfferId(), message, user);
        driverItem.setDescription(changeOfferRequest.getDescription());
        driverItemRepository.save(driverItem);
    }

    @Override
    @Transactional
    public void deleteBySecondDateAfter(LocalDate date) {
        log.info("Delete drivers whose second date is earlier than : {}", date);
        driverItemRepository.deleteOffersBySecondDateBeforeAndSecondDateIsNotNull(date);
    }

    @Override
    @Transactional
    public void deleteByFirstDateAfterWhereSecondDateIsNull(LocalDate date) {
        log.info("Delete offers whose first date is earlier than :{} and second date is null", date);
        driverItemRepository.deleteOffersByFirstDateBeforeAndSecondDateIsNull(date);
    }


    private boolean checkRoute(List<String> driverCities, DriverItem offer, Activity activity) {
        boolean result = false;
        int difference = driverCities.size() < offer.getCities().size() ?
                driverCities.size() - offer.getCities().size() :
                offer.getCities().size() - driverCities.size();
        List<String> transferCities = offer.getCities().stream()
                .map(City::getName)
                .collect(Collectors.toList());
        int matches = 0;
        int previousSupplierIndex = -1;
        if (activity == Activity.PASSENGER) {
            result = checkRouteForPassenger(driverCities, result, difference, transferCities, matches, previousSupplierIndex);
        } else {
            result = checkRouteForParcel(driverCities, result, difference, transferCities, matches, previousSupplierIndex);
        }
        return result;
    }


}
