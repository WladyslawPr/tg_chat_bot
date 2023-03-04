package com.dev.vlpr.service.impl;

import com.dev.vlpr.crypto.CryptoTool;
import com.dev.vlpr.dao.AppUserDAO;
import com.dev.vlpr.entity.AppUser;
import com.dev.vlpr.service.UserActivationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserActivationServiceImpl implements UserActivationService {

    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;

    public UserActivationServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool) {
        this.appUserDAO = appUserDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public boolean activation (String cryptoUserId) {
        var userId = cryptoTool.idOf(cryptoUserId);
        Optional<AppUser> optionalAppUser = appUserDAO.findById(userId);
        if (optionalAppUser.isPresent()) {
            var user = optionalAppUser.get();
            appUserDAO.save(user);
            return true;
        }
        return false;
    }
}
