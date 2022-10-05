package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.util.CallbackUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CANCEL_FIRST_DATE_OF_OFFER;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelFirstDateOfOfferCallbackType implements CallbackType {

    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return CANCEL_FIRST_DATE_OF_OFFER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        LocalDate canceledDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        log.info("Got {} callback type with canceled date :{} and token: {}", CANCEL_FIRST_DATE_OF_OFFER,canceledDate,token);
        requestService.getChangeOfferRequest(callbackQuery.getMessage(),token );
        return CallbackUtil.DateUtil.createEditMessageTextForFirstDate(callbackQuery, CHANGE_FIRST_DATE_OF_OFFER.ordinal(),
                RETURN_TO_CHANGE_OF_OFFER.ordinal(),"You've canceled the first date", canceledDate,token );
    }
}
