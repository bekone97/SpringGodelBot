package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.CityService;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CANCEL_DRIVER_ROUTE;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.DRIVER_ROUTE;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.createEditSendMessageForRoutes;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelDriverRouteCallbackType implements CallbackType {
    private final CityService cityService;
    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return Callbacks.CANCEL_DRIVER_ROUTE.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        int routeId = Integer.parseInt(getCallbackValue(callbackQuery.getData()));
        log.info("Callback data with type: {} and routeId: {} and token : {}", DRIVER_ROUTE, routeId,token);
        List<City> cities = cityService.findAll();
        City reservedRoute = cities.stream()
                .filter(route -> route.getId().equals(routeId))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        DriverRequest supplerRequest = requestService.getDriverRequest(callbackQuery.getMessage(),token );
        supplerRequest.getCities().remove(reservedRoute);
        return createEditSendMessageForRoutes(callbackQuery, cities, supplerRequest.getCities(),
                DRIVER_ROUTE.ordinal(), CANCEL_DRIVER_ROUTE.ordinal(),token );
    }
}
