package com.example.telegramleadbot.unit.service;

import com.example.telegramleadbot.entity.enums.BotEventType;
import com.example.telegramleadbot.entity.enums.LeadSource;
import com.example.telegramleadbot.repository.BotEventRepository;
import com.example.telegramleadbot.service.StatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatsServiceTest {

    @Mock
    private BotEventRepository botEventRepository;

    @InjectMocks
    private StatsService statsService;

    @Test
    @DisplayName("Успешный сбор статистики вызовов")
    void successfulCollectionOfCallStatistics() {

        when(botEventRepository.countByEventType(BotEventType.START)).thenReturn(5L);
        when(botEventRepository.countByEventType(BotEventType.REQUEST_COMPLETED)).thenReturn(2L);
        when(botEventRepository.countByEventType(BotEventType.REQUEST_STARTED)).thenReturn(4L);

        when(botEventRepository.countByEventTypeAndSource(BotEventType.START, LeadSource.UNKNOWN)).thenReturn(3L);
        when(botEventRepository.countByEventTypeAndSource(BotEventType.REQUEST_STARTED, LeadSource.UNKNOWN)).thenReturn(1L);
        when(botEventRepository.countByEventTypeAndSource(BotEventType.REQUEST_COMPLETED, LeadSource.UNKNOWN)).thenReturn(1L);

        when(botEventRepository.countByEventTypeAndSource(BotEventType.START, LeadSource.AVITO)).thenReturn(0L);
        when(botEventRepository.countByEventTypeAndSource(BotEventType.REQUEST_STARTED, LeadSource.AVITO)).thenReturn(0L);
        when(botEventRepository.countByEventTypeAndSource(BotEventType.REQUEST_COMPLETED, LeadSource.AVITO)).thenReturn(0L);

        when(botEventRepository.countByEventTypeAndSource(BotEventType.START, LeadSource.FL)).thenReturn(1L);
        when(botEventRepository.countByEventTypeAndSource(BotEventType.REQUEST_STARTED, LeadSource.FL)).thenReturn(1L);
        when(botEventRepository.countByEventTypeAndSource(BotEventType.REQUEST_COMPLETED, LeadSource.FL)).thenReturn(1L);

        String result = statsService.buildStatsMessage();

        verify(botEventRepository, times(1)).countByEventType(BotEventType.START);
        verify(botEventRepository, times(1)).countByEventType(BotEventType.REQUEST_STARTED);
        verify(botEventRepository, times(1)).countByEventType(BotEventType.REQUEST_COMPLETED);

        verify(botEventRepository, times(1)).countByEventTypeAndSource(BotEventType.START, LeadSource.UNKNOWN);
        verify(botEventRepository, times(1)).countByEventTypeAndSource(BotEventType.REQUEST_STARTED, LeadSource.UNKNOWN);
        verify(botEventRepository, times(1)).countByEventTypeAndSource(BotEventType.REQUEST_COMPLETED, LeadSource.UNKNOWN);
        verify(botEventRepository, times(1)).countByEventTypeAndSource(BotEventType.START, LeadSource.AVITO);
        verify(botEventRepository, times(1)).countByEventTypeAndSource(BotEventType.REQUEST_STARTED, LeadSource.AVITO);
        verify(botEventRepository, times(1)).countByEventTypeAndSource(BotEventType.REQUEST_COMPLETED, LeadSource.AVITO);
        verify(botEventRepository, times(1)).countByEventTypeAndSource(BotEventType.START, LeadSource.FL);
        verify(botEventRepository, times(1)).countByEventTypeAndSource(BotEventType.REQUEST_STARTED, LeadSource.FL);
        verify(botEventRepository, times(1)).countByEventTypeAndSource(BotEventType.REQUEST_COMPLETED, LeadSource.FL);

        assertEquals(5L, botEventRepository.countByEventType(BotEventType.START));
        assertEquals(2L, botEventRepository.countByEventType(BotEventType.REQUEST_COMPLETED));
        assertEquals(4L, botEventRepository.countByEventType(BotEventType.REQUEST_STARTED));

        assertTrue(result.contains("UNKNOWN"));
        assertTrue(result.contains("AVITO"));
        assertTrue(result.contains("FL"));

        assertTrue(result.contains("Запусков"));
        assertTrue(result.contains("Начали заявку"));
        assertTrue(result.contains("Завершили заявку"));


    }

    @Test
    @DisplayName("успешный сценарий статистики за прошедшие день")
    void successStatisticsForThePastDay() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.plusDays(1).atStartOfDay();

        when(botEventRepository.countByEventTypeAndCreatedAtBetween(BotEventType.START, start, end)).thenReturn(5L);
        when(botEventRepository.countByEventTypeAndCreatedAtBetween(BotEventType.REQUEST_STARTED, start, end)).thenReturn(5L);
        when(botEventRepository.countByEventTypeAndCreatedAtBetween(BotEventType.REQUEST_COMPLETED, start, end)).thenReturn(3L);

        String value = statsService.buildYesterdayStatsMessage();

        ArgumentCaptor<LocalDateTime> captorStart = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> captorEnd = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(botEventRepository, times(3)).countByEventTypeAndCreatedAtBetween(any(BotEventType.class), captorStart.capture(), captorEnd.capture());

        LocalDateTime actualStart = captorStart.getValue();
        LocalDateTime actualEnd = captorEnd.getValue();

        assertEquals(start, actualStart);
        assertEquals(end, actualEnd);

        assertTrue(value.contains("Запусков бота: 5"));
        assertTrue(value.contains("Начали заявку: 5"));
        assertTrue(value.contains("Завершили заявку: 3"));

    }

}
