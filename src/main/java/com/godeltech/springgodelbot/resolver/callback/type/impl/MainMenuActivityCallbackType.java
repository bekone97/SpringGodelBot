package com.godeltech.springgodelbot.resolver.callback.type.impl;

import com.godeltech.springgodelbot.mapper.UserMapper;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.MAIN_MENU;
import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;
import static com.godeltech.springgodelbot.util.CallbackUtil.makeSendMessageForUserWithoutUsername;

@Component
@RequiredArgsConstructor
public class MainMenuActivityCallbackType implements CallbackType {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public String getCallbackName() {
        return MAIN_MENU.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        if (callbackQuery.getFrom().getUserName()==null)
            return makeSendMessageForUserWithoutUsername(callbackQuery.getMessage());
        if (!userService.existsByIdAndUsername(callbackQuery.getFrom().getId(),callbackQuery.getFrom().getUserName()))
            userService.save(userMapper.mapToUserEntity(callbackQuery.getFrom()), callbackQuery.getMessage());
        return getStartMenu(callbackQuery);
    }

}
