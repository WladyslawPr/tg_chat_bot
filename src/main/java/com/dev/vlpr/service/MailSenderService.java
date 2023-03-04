package com.dev.vlpr.service;

import com.dev.vlpr.dto.MailParamsDTO;

public interface MailSenderService {
    void send (MailParamsDTO mailParamsDTO);
}
