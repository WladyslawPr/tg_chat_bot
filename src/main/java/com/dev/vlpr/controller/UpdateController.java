package com.dev.vlpr.controller;

import com.dev.vlpr.bot.TelegramBot;
import com.dev.vlpr.service.UpdateProducer;
import com.dev.vlpr.utils.MessageUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.dev.vlpr.common.RabbitQueue.*;
import static com.dev.vlpr.constants.ResponseConstants.*;

@Component
@Log4j
public class UpdateController {

    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils,
                            UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }
    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    // initial validation of incoming data.
    public void processUpdate(Update update) {
        if (update == null) {
            log.error(RECEIVED_UPDATE_IS_NULL);
            return;
        }
        //processing unedited messages from private chats.
        if (update.getMessage() != null) {
            distributeMessagesByType(update);
        } else {
           // log.error("received unsupported message type " + update);
            log.error(RECEIVED_UNSUPPORTED_MESSAGE_TYPE.getMessage() + update);
        }
    }
    // distribute messages by types of incoming data.
    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();
        if (message.getText() != null) {
            processTextMessage(update);
        } else if (message.getDocument() != null) {
            processDocMessage(update);
        } else if (message.getPhoto() != null) {
            processPhotoMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }

    private void  setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils
                .generateSendMessageWithText(update, //"unsupported message type");
                        UNSUPPORTED_MESSAGE_TYPE.getMessage());
        setView(sendMessage);
    }

    private void setFileIsReceivedView(Update update) {
        var sendMessage = messageUtils
                .generateSendMessageWithText(update,// "file accept");
                        FILE_ACCEPTED_PROCESSED.getMessage());
        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }

    private void processDocMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }


}

