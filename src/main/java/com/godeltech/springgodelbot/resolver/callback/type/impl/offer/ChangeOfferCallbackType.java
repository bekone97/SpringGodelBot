package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHANGE_OFFER;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangeOfferCallbackType implements CallbackType {
    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return CHANGE_OFFER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        long offerId = Long.parseLong(getCallbackValue(callbackQuery.getData()));
        log.info("Got {} callback type with route id :{} and token: {} by user :{}",
                CHANGE_OFFER, offerId, token, callbackQuery.getFrom().getUserName());
        Request request = requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());
        request = requestService.setOfferToRequest(offerId, request, callbackQuery.getMessage(), callbackQuery.getFrom());
        String textMessage = getOffersView(request);
        return getEditTextMessageForOffer(callbackQuery, token, request, textMessage);
    }


}
