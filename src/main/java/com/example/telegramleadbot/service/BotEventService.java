package com.example.telegramleadbot.service;

import com.example.telegramleadbot.entity.BotEventEntity;
import com.example.telegramleadbot.entity.enums.BotEventType;
import com.example.telegramleadbot.entity.enums.LeadSource;
import com.example.telegramleadbot.repository.BotEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BotEventService {

    private final BotEventRepository botEventRepository;

    public void saveEvent(Long chatId, LeadSource source, BotEventType eventType) {
        BotEventEntity event = new BotEventEntity();
        event.setChatId(chatId);
        event.setSource(source);
        event.setEventType(eventType);
        event.setCreatedAt(LocalDateTime.now());

        botEventRepository.save(event);
    }
}