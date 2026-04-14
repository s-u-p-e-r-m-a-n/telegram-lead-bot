package com.example.telegramleadbot.repository;

import com.example.telegramleadbot.entity.BotEventEntity;
import com.example.telegramleadbot.entity.enums.BotEventType;
import com.example.telegramleadbot.entity.enums.LeadSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface BotEventRepository extends JpaRepository<BotEventEntity, Long> {

    long countByEventType(BotEventType eventType);

    long countByEventTypeAndCreatedAtBetween(
            BotEventType eventType,
            LocalDateTime start,
            LocalDateTime end
    );

    long countByEventTypeAndSource(BotEventType eventType, LeadSource source);


}
