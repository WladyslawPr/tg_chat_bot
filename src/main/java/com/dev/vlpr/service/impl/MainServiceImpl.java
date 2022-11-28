package com.dev.vlpr.service.impl;

import com.dev.vlpr.dao.AppUserDAO;
import com.dev.vlpr.dao.RawDataDAO;
import com.dev.vlpr.entity.AppUsers;
import com.dev.vlpr.entity.RawData;
import com.dev.vlpr.entity.enums.UserState;
import com.dev.vlpr.service.MainService;
import com.dev.vlpr.service.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Service
public class MainServiceImpl implements MainService {
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

        var message = update.getMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("hi from node");
        producerService.producerAnswer(sendMessage);

    }


    private void saveRowData(Update update) {
        RawData rawData = RawData.builder()
                .update(update)
                .build();
        rawDataDAO.save(rawData);
    }

    private AppUsers findOrSaveAppUser(User telegramUser) {
      //  User telegramUser = update.getMessage().getFrom();
        AppUsers persistentAppUser = appUserDAO.findAppUsersByTelegramUserId(telegramUser.getId());

        if (persistentAppUser == null) {
            AppUsers transientAppUser = AppUsers.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //TODO change default value after adding registration
                    .isActive(true)
                    .state(UserState.BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return persistentAppUser;
    }


}
