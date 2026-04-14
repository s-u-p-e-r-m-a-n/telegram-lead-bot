package com.example.telegramleadbot.scheduler;

import com.example.telegramleadbot.config.TelegramBotProperties;
import com.example.telegramleadbot.service.StatsService;
import com.example.telegramleadbot.telegram.TelegramMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatsScheduler {

    private final StatsService statsService;
    private final TelegramMessageSender telegramMessageSender;
    private final TelegramBotProperties telegramBotProperties;

    @Scheduled(cron = "${app.stats.cron}")
    public void sendMorningStats() {
        log.info("Sending morning stats to admin");

        telegramMessageSender.sendText(
                telegramBotProperties.getAdminChatId(),
                statsService.buildYesterdayStatsMessage()
        );
    }
}