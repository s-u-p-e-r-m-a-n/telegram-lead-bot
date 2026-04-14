package com.example.telegramleadbot.repository;

import com.example.telegramleadbot.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSessionEntity, Long> {

    Optional<UserSessionEntity> findByChatId(Long chatId);

}
