package com.dev.vlpr.service.impl;

import com.dev.vlpr.dto.MailParamsDTO;
import com.dev.vlpr.service.MailSenderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailSenderServiceImpl implements MailSenderService {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String emailFrom;
    @Value("${service.activation.uri}")
    private String activationServiceUri;

    public MailSenderServiceImpl (JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void send (MailParamsDTO mailParamsDTO) {
        var subject = "Account activation";
        var messageBody = getActivationMailBody(mailParamsDTO.getId());
        var emailTo = mailParamsDTO.getEmailTo();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(messageBody);

        javaMailSender.send(mailMessage);
    }

    private String getActivationMailBody (String id) {
        var msg = String.format("To complete the registration follow the link:\n%s",
                activationServiceUri);
        return msg.replace("{id}", id);
    }

}