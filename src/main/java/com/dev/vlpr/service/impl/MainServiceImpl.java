package com.dev.vlpr.service.impl;

import com.dev.vlpr.dao.AppUserDAO;
import com.dev.vlpr.dao.RawDataDAO;
import com.dev.vlpr.entity.AppDocument;
import com.dev.vlpr.entity.AppPhoto;
import com.dev.vlpr.entity.AppUser;
import com.dev.vlpr.entity.RawData;
import com.dev.vlpr.entity.enums.LinkType;
import com.dev.vlpr.entity.enums.ServiceCommand;
import com.dev.vlpr.exceptions.UploadFileException;
import com.dev.vlpr.service.AppUserService;
import com.dev.vlpr.service.FileService;
import com.dev.vlpr.service.MainService;
import com.dev.vlpr.service.ProducerService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

import static com.dev.vlpr.entity.enums.ServiceCommand.*;
import static com.dev.vlpr.entity.enums.UserState.BASIC_STATE;
import static com.dev.vlpr.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
@Log4j
@Service
public class MainServiceImpl implements MainService {

    private static final String UNKNOWN_USER_STATE = "unknown user state";
    private static final String ERROR_RETRY_INPUT = "input /cancel and try again";
    private static final String REJECT_COMMAND = "reject command";
    private static final String WELCOME = "Welcome: To see a list of available commands type /help";
    private static final String UNKNOWN_COMMAND = "unknown command, to see a list of available commands type /help";
    private static final String DOC_UPLOAD_SUCCESS = "document uploaded successfully: link to download -  ";
    private static final String DOC_UPLOAD_FAILED = "file upload failed, please try again";
    private static final String PHOTO_UPLOAD_SUCCESS = "photo uploaded successfully: link to download -  ";
    private static final String PHOTO_UPLOAD_FAILED = "photo upload failed, please try again";
    private static final String CANCEL_CURRENT_COMMAND = "cancel the current command with /cancel to send files.";
    private static final String REGISTER_OR_ACTIVATE = "register or activate your account to download content.";
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;
    private final AppUserService appUserService;

    public MainServiceImpl(RawDataDAO rawDataDAO,
                           ProducerService producerService,
                           AppUserDAO appUserDAO,
                           FileService fileService,
                           AppUserService appUserService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
        this.appUserService = appUserService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        var serviceCommand = ServiceCommand.fromValue(text);
        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            output = appUserService.setEmail(appUser, text);
        } else {
            log.error(UNKNOWN_USER_STATE + userState);
            output = ERROR_RETRY_INPUT;
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);

    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }
        try {
            AppDocument document = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(document.getId(), LinkType.GET_DOC);
            var answer = DOC_UPLOAD_SUCCESS + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException exception) {
            log.error(exception);
            sendAnswer(DOC_UPLOAD_FAILED, chatId);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }
        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            var answer = PHOTO_UPLOAD_SUCCESS + link;

            sendAnswer(answer, chatId);
        } catch (UploadFileException exception) {
            log.error(exception);
            sendAnswer(PHOTO_UPLOAD_FAILED, chatId);

        }

    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if (!appUser.getIsActive()) {
            sendAnswer(REGISTER_OR_ACTIVATE, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            sendAnswer(CANCEL_CURRENT_COMMAND, chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return REJECT_COMMAND;
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        var serviceCommand = ServiceCommand.fromValue(cmd);
        if (REGISTRATION.equals(serviceCommand)) {
            return appUserService.register(appUser);
        } else if (HELP.equals(serviceCommand)) {
            return help();
        } else if (START.equals(serviceCommand)) {
            return WELCOME;
        } else {
            return UNKNOWN_COMMAND;
        }

    }

    private String help() {
        return "List of available commands;\n"
                + "/cancel - canceling the current command;\n"
                + "/registration - registration users.";
    }

    private void saveRawData (Update update) {
        RawData rawData = RawData.builder()
                .update(update)
                .build();
        rawDataDAO.save(rawData);
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        Optional<AppUser> persistentAppUser = appUserDAO.findByTelegramUserId(telegramUser.getId());

        if (persistentAppUser.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(false)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return persistentAppUser.get();
    }


}
