package com.godeltech.springgodelbot.service;

import com.godeltech.springgodelbot.model.entity.*;
import com.godeltech.springgodelbot.model.entity.enums.Activity;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.util.List;

public interface TransferItemService {
    List<TransferItem> findTransferItemsByFirstDateBeforeAndSecondDateAfterAndCities
            (LocalDate secondDate, LocalDate firstDate, List<String> cities);

    List<ChangeOfferRequest> findByUserEntityIdAndActivity(Long id, Activity activity, Message message, User user);

    ChangeOfferRequest getById(Long offerId, Message message, User user);

    void deleteById(Long offerId, Message message, User user);

    void updateCitiesOfTransferItem(ChangeOfferRequest changeOfferRequest, Message message, User user);

    void updateDatesOfTransferItem(ChangeOfferRequest changeOfferRequest, Message message, User user);

    void updateDescriptionOfTransferItem(ChangeOfferRequest changeOfferRequest, Message message, User user);

    void deleteBySecondDateAfter(LocalDate now);

    void deleteByFirstDateAfterWhereSecondDateIsNull(LocalDate date);

    TransferItem save(Request request, User user, Message message);

    List<TransferItem> findTransferItemsByFirstDateBeforeAndSecondDateAfterAndCitiesAndActivity(LocalDate secondDate, LocalDate firstDate, List<String> cities, Activity activity);
}
