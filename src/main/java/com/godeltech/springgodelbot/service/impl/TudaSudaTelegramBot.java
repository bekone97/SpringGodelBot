package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.exception.UnknownCommandException;
import com.godeltech.springgodelbot.service.CallbackResolverService;
import com.godeltech.springgodelbot.service.MessageResolverService;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.util.Set;

@Slf4j
@Getter
@Setter
public class TudaSudaTelegramBot extends SpringWebhookBot {

    private final MessageResolverService messageResolverService;
    private final CallbackResolverService callbackResolverService;

    private String botUsername;
    private String botToken;
    private String botPath;

    public TudaSudaTelegramBot(SetWebhook setWebhookInstance, MessageResolverService messageResolverService, CallbackResolverService callbackResolverService) {
        super(setWebhookInstance);
        this.messageResolverService = messageResolverService;
        this.callbackResolverService = callbackResolverService;
    }

    public TudaSudaTelegramBot(SetWebhook setWebhookInstance, DefaultBotOptions defaultBotOptions, MessageResolverService messageResolverService, CallbackResolverService callbackResolverService) {
        super(defaultBotOptions, setWebhookInstance);
        this.messageResolverService = messageResolverService;
        this.callbackResolverService = callbackResolverService;
    }

    @Override
    @SneakyThrows
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        BotApiMethod botApiMethod = null;
        if (update.hasMessage()) {
            botApiMethod = messageResolverService.handleMessage(update.getMessage());
            return botApiMethod;
        } else if (update.hasCallbackQuery()) {
            botApiMethod = callbackResolverService.handleCallBack(update.getCallbackQuery());
            return botApiMethod;
        }
        return botApiMethod;
    }
    public void editPreviousMessage(CallbackQuery callbackQuery, String answer) {
        try {
            log.info("Edit previous message with message Id : {}",callbackQuery.getMessage().getMessageId());
            execute(EditMessageText.builder()
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .text(answer)
                    .build());
        } catch (TelegramApiException e) {
            log.error("There is a tg exception: {}", e.getMessage());
            throw new UnknownCommandException(callbackQuery.getMessage());
        }
    }

    public void deleteMessages(Long chatId, Set<Integer> messages) {
        log.info("Delete messages with chat id : {}",chatId);
        messages.forEach(message ->
                deleteMessage(chatId, message)
        );
        messages.clear();
    }

    public void deleteMessage(Long chatId, Integer message) {
        try {
            execute(DeleteMessage.builder()
                    .chatId(chatId.toString())
                    .messageId(message)
                    .build());
        } catch (TelegramApiException e) {
            throw new UnknownCommandException();
        }
    }
}