package com.dev.vlpr.service.impl;

import com.dev.vlpr.crypto.CryptoTool;
import com.dev.vlpr.dao.AppUserDAO;
import com.dev.vlpr.dto.MailParamsDTO;
import com.dev.vlpr.entity.AppUser;
import com.dev.vlpr.entity.enums.UserState;
import com.dev.vlpr.service.AppUserService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Optional;

@Log4j
@Service
public class AppUserServiceImpl implements AppUserService {

    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;
    @Value("${service.mail.uri}")
    private String mailServiceUri;

    public AppUserServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool) {
        this.appUserDAO = appUserDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public String register (AppUser appUser) {
        if (appUser.getIsActive()) {
            return "You are already registered";
        } else if (appUser.getEmail() != null) {
            return "An email has been sent to you " + "follow the link in the email to confirm your registration";
        }
        appUser.setState(UserState.WAIT_FOR_EMAIL_STATE);
        appUserDAO.save(appUser);
        return "Please enter your email";
    }

    @Override
    public String setEmail (AppUser appUser, String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException exception) {
            return "Please enter a valid email. To cancel the command, type /cancel";
        }
        Optional<AppUser> optionalAppUser = appUserDAO.findByEmail(email);
        if (optionalAppUser.isEmpty()) {
            appUser.setEmail(email);
            appUser.setState(UserState.BASIC_STATE);
            appUser = appUserDAO.save(appUser);

            var cryptoUserId = cryptoTool.hashOf(appUser.getId());
            var response = sendRequestToMailService(cryptoUserId, email);
            if (response.getStatusCode() != HttpStatus.OK) {
                var msg = String.format("Email sending %s failed", email);
                log.error(msg);
                appUser.setEmail(null);
                appUserDAO.save(appUser);
                return msg;
            }
            return  "An email has been sent to you," +
                    " follow the link in the email to confirm your registration.";
        } else {
            return "This email is already in use. Enter a valid email. " +
                    "To cancel the command, type /cancel";
        }
    }

    private ResponseEntity<String> sendRequestToMailService (String cryptoUserId, String email) {
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        MailParamsDTO mailParamsDTO = MailParamsDTO.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();

        var request = new HttpEntity<MailParamsDTO>(mailParamsDTO, headers);
        return restTemplate.exchange(mailServiceUri,
                HttpMethod.POST,
                request,
                String.class);
    }

}
