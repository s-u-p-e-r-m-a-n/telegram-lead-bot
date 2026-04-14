package com.example.telegramleadbot.service;

import com.example.telegramleadbot.entity.enums.BotEventType;
import com.example.telegramleadbot.entity.enums.LeadSource;
import com.example.telegramleadbot.repository.BotEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final BotEventRepository botEventRepository;

    public String buildStatsMessage() {
        long starts = botEventRepository.countByEventType(BotEventType.START);
        long startedRequests = botEventRepository.countByEventType(BotEventType.REQUEST_STARTED);
        long completedRequests = botEventRepository.countByEventType(BotEventType.REQUEST_COMPLETED);

        long flStarts = botEventRepository.countByEventTypeAndSource(BotEventType.START, LeadSource.FL);
        long flStarted = botEventRepository.countByEventTypeAndSource(BotEventType.REQUEST_STARTED, LeadSource.FL);
        long flCompleted = botEventRepository.countByEventTypeAndSource(BotEventType.REQUEST_COMPLETED, LeadSource.FL);

        long avitoStarts = botEventRepository.countByEventTypeAndSource(BotEventType.START, LeadSource.AVITO);
        long avitoStarted = botEventRepository.countByEventTypeAndSource(BotEventType.REQUEST_STARTED, LeadSource.AVITO);
        long avitoCompleted = botEventRepository.countByEventTypeAndSource(BotEventType.REQUEST_COMPLETED, LeadSource.AVITO);

        long unknownStarts = botEventRepository.countByEventTypeAndSource(BotEventType.START, LeadSource.UNKNOWN);
        long unknownStarted = botEventRepository.countByEventTypeAndSource(BotEventType.REQUEST_STARTED, LeadSource.UNKNOWN);
        long unknownCompleted = botEventRepository.countByEventTypeAndSource(BotEventType.REQUEST_COMPLETED, LeadSource.UNKNOWN);

        return """
                Статистика:
                
                Общая:
                Запусков бота: %s
                Начали заявку: %s
                Завершили заявку: %s
                
                FL:
                Start: %s
                Request started: %s
                Request completed: %s
                
                AVITO:
                Start: %s
                Request started: %s
                Request completed: %s
                
                UNKNOWN:
                Start: %s
                Request started: %s
                Request completed: %s
                """.formatted(
                starts,
                startedRequests,
                completedRequests,
                flStarts,
                flStarted,
                flCompleted,
                avitoStarts,
                avitoStarted,
                avitoCompleted,
                unknownStarts,
                unknownStarted,
                unknownCompleted
        );
    }

    public String buildYesterdayStatsMessage() {
        /** Определяем дату */
        LocalDate yesterday = LocalDate.now().minusDays(1);
        /** Определяем вчерашний день с 00:00 */
        LocalDateTime start = yesterday.atStartOfDay();
        /**  Определяем начало сегодняшнего дня */
        LocalDateTime end = yesterday.plusDays(1).atStartOfDay();
        /** Поиск событий типа START
         у которых createdAt
         находится между start и end
         и считаем их количество */
        long starts = botEventRepository.countByEventTypeAndCreatedAtBetween(
                BotEventType.START, start, end
        );
        long startedRequests = botEventRepository.countByEventTypeAndCreatedAtBetween(
                BotEventType.REQUEST_STARTED, start, end
        );
        long completedRequests = botEventRepository.countByEventTypeAndCreatedAtBetween(
                BotEventType.REQUEST_COMPLETED, start, end
        );

        return """
                Утренняя статистика за %s:
                
                Запусков бота: %s
                Начали заявку: %s
                Завершили заявку: %s
                """.formatted(
                yesterday,
                starts,
                startedRequests,
                completedRequests
        );
    }
}