package com.dev.vlpr.service;

import com.dev.vlpr.entity.AppUser;

public interface AppUserService {

    String register(AppUser appUser);
    String setEmail(AppUser appUser, String email);

}
