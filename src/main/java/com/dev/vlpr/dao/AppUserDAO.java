package com.dev.vlpr.dao;

import com.dev.vlpr.entity.AppUsers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserDAO extends JpaRepository<AppUsers, Long> {
    AppUsers findAppUsersByTelegramUserId(Long id);
}
