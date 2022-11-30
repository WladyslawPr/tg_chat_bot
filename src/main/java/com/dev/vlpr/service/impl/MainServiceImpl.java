package com.dev.vlpr.service.impl;

import com.dev.vlpr.dao.AppUserDAO;
import com.dev.vlpr.dao.RawDataDAO;
import com.dev.vlpr.entity.AppUsers;
import com.dev.vlpr.entity.RawData;
import com.dev.vlpr.entity.enums.UserState;
import com.dev.vlpr.service.MainService;
import com.dev.vlpr.service.ProducerService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.dev.vlpr.entity.enums.ServiceCommand.CANCEL;
import static com.dev.vlpr.entity.enums.UserState.BASIC_STATE;
import static com.dev.vlpr.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
@Log4j
@Service
public class MainServiceImpl implements MainService {

    private static final String UNKNOWN_USER_STATE = "unknown user state";
    private static final String ERROR_RETRY_INPUT = "input /cancel and try again";
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;

    public MainServiceImpl(RawDataDAO rawDataDAO,
                           ProducerService producerService,
                           AppUserDAO appUserDAO) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRowData(update);
      //  var textMessage = update.getMessage();
      //  var telegramUser = textMessage.getFrom();
        var appUser = findOrSaveAppUser(update);

        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";
        
        if (CANCEL.equals(text)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            //TODO add processing email.
        } else {
            log.error(UNKNOWN_USER_STATE + userState);
            output = ERROR_RETRY_INPUT;
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);

    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    private String cancelProcess(AppUsers appUser) {
        return null;
    }

    private String processServiceCommand(AppUsers appUser, String text) {
        return null;
    }


    private void saveRowData(Update update) {
        RawData rawData = RawData.builder()
                .update(update)
                .build();
        rawDataDAO.save(rawData);
    }


    private AppUsers findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        AppUsers persistentAppUser = appUserDAO.findAppUsersByTelegramUserId(telegramUser.getId());

        if (persistentAppUser == null) {
            AppUsers transientAppUser = AppUsers.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //TODO change default value after adding registration
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return persistentAppUser;
    }


}
