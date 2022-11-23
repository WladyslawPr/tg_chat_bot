package com.dev.vlpr.controller;

import com.dev.vlpr.bot.TelegramBot;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.dev.vlpr.constants.ResponseConstants.RECEIVED_UNSUPPORTED_MESSAGE_TYPE;
import static com.dev.vlpr.constants.ResponseConstants.RECEIVED_UPDATE_IS_NULL;

@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;
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
            log.error(RECEIVED_UNSUPPORTED_MESSAGE_TYPE + " " + update);
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

    private void setUnsupportedMessageTypeView(Update update) {
    }

    private void processPhotoMessage(Update update) {
    }

    private void processDocMessage(Update update) {
    }

    private void processTextMessage(Update update) {
    }


}
