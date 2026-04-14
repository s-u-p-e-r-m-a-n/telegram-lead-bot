package com.example.telegramleadbot.entity;

import com.example.telegramleadbot.entity.enums.BotEventType;
import com.example.telegramleadbot.entity.enums.LeadSource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bot_events")
@Getter
@Setter
public class BotEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private LeadSource source;


    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private BotEventType eventType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}