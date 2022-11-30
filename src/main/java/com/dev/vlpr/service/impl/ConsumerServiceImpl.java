package com.dev.vlpr.service.impl;

import com.dev.vlpr.service.ConsumerService;
import com.dev.vlpr.service.MainService;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.dev.vlpr.common.RabbitQueue.*;

@Service
@Log4j
public class ConsumerServiceImpl implements ConsumerService {

    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumerTextMessageUpdate(Update update) {
        log.debug("node: txt message is received");
        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumerDocMessageUpdate(Update update) {
        log.debug("node: doc message is received");
        mainService.processDocMessage(update);

    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumerPhotoMessageUpdate(Update update) {
        log.debug("node: photo message is received");
        mainService.processPhotoMessage(update);

    }
}
