package com.dev.vlpr.service.impl;

import com.dev.vlpr.dao.RawDataDAO;
import com.dev.vlpr.entity.RawData;
import com.dev.vlpr.service.MainService;
import com.dev.vlpr.service.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
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
}
