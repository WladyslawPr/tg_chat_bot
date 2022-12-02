package com.dev.vlpr.service;

import com.dev.vlpr.entity.AppDocument;
import com.dev.vlpr.entity.AppPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
}
