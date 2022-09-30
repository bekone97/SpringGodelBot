package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.util.CallbackUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelFirstDateOfOfferCallbackType implements CallbackType {

    private final RequestService requestService;

    @Override
    public String getCallbackName() {
        return Callbacks.CANCEL_FIRST_DATE_OF_OFFER.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        LocalDate canceledDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        requestService.getChangeOfferRequest(callbackQuery.getMessage());
        return CallbackUtil.DateUtil.createEditMessageTextForFirstDate(callbackQuery, Callbacks.CHANGE_FIRST_DATE_OF_OFFER.name(),
                "You've canceled the first date", canceledDate);
    }
}
