package com.dev.vlpr.controller.rest;

import com.dev.vlpr.dto.MailParamsDTO;
import com.dev.vlpr.service.MailSenderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/mail")
@RestController
public class MailController {

    private final MailSenderService mailSenderService;

    public MailController(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendActivationMail(@RequestBody MailParamsDTO mailParamsDTO) {
        mailSenderService.send(mailParamsDTO);
        return ResponseEntity.ok().build();
    }

}