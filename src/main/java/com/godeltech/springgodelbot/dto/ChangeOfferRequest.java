package com.godeltech.springgodelbot.dto;

import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.model.entity.City;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class ChangeOfferRequest extends Request {
    private Long offerId;


    @Builder
    public ChangeOfferRequest(Long chatId, UserDto userDto,
                              List<City> cities, LocalDate firstDate,
                              LocalDate secondDate, String description, Boolean needForDescription,
                              Set<Integer> messages, Activity activity, Long offerId) {
        super(chatId, userDto, cities, firstDate, secondDate, description, needForDescription, messages, activity);
        this.offerId = offerId;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }
}