package com.dev.vlpr.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProducer {

    // try to accept response from RabbitMQ and pass it to UpdateController.
    void produce(String rabbitQueue, Update update);
}
