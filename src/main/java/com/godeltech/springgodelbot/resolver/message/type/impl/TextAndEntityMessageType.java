package com.godeltech.springgodelbot.resolver.message.type.impl;

import com.godeltech.springgodelbot.exception.UnknownCommandException;
import com.godeltech.springgodelbot.exception.UserAuthorizationException;
import com.godeltech.springgodelbot.mapper.UserMapper;
import com.godeltech.springgodelbot.resolver.message.Messages;
import com.godeltech.springgodelbot.resolver.message.type.MessageType;
import com.godeltech.springgodelbot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;
import static com.godeltech.springgodelbot.util.CallbackUtil.makeEditMessageForUserWithoutUsername;
import static com.godeltech.springgodelbot.util.CallbackUtil.makeSendMessageForUserWithoutUsername;
import static com.godeltech.springgodelbot.util.ConstantUtil.START_MESSAGE;

@Component
@RequiredArgsConstructor
@Slf4j
public class TextAndEntityMessageType implements MessageType {
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public String getMessageType() {
        return Messages.TEXT_AND_ENTITY.name();
    }

    @Override
    public BotApiMethod createSendMessage(Message message) {
        return getSendMessage(message);
    }


    private BotApiMethod getSendMessage(Message message) {
        log.error("Got text message with entity :{}",message.getText());
        Optional<MessageEntity> commandEntity = message.getEntities().stream()
                .filter(entity -> "bot_command".equals(entity.getType()))
                .findFirst();
        if (commandEntity.isPresent()) {
            String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
            switch (command) {
                case "/start":
                    return makeSendMessageForUser(message);
                case "/help":
                    return makeHelpSendMessage(message);
                default:
                    throw new UnknownCommandException(message);
            }
        } else {
            throw new UnknownCommandException(message);
        }
    }

    private SendMessage makeHelpSendMessage(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("This bot was created for people who need help moving to some place or for those who can offer this help")
                .build();
    }

    private BotApiMethod makeSendMessageForUser(Message message) {
        if (message.getFrom().getUserName() == null) {
            log.info("User has null username");
            throw new UserAuthorizationException(User.class,"username",null,message,true);
        }
        if (!userService.existsByIdAndUsername(message.getFrom().getId(), message.getFrom().getUserName()))
            userService.save(userMapper.mapToUserEntity(message.getFrom()), message);
        return getStartMenu(message.getChatId(), START_MESSAGE);
    }

}
