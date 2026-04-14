package com.example.telegramleadbot.config;

import com.example.telegramleadbot.telegram.TelegramUpdateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class TelegramBotConfig {

    @Bean
    public TelegramClient telegramClient(TelegramBotProperties telegramBotProperties) {
        return new OkHttpTelegramClient(telegramBotProperties.getToken());
    }

    @Bean
    public TelegramBotsLongPollingApplication telegramBotsApplication(
            TelegramBotProperties telegramBotProperties,
            TelegramUpdateHandler telegramUpdateHandler
    ) throws TelegramApiException {
        TelegramBotsLongPollingApplication application = new TelegramBotsLongPollingApplication();
        application.registerBot(
                telegramBotProperties.getToken(),
                telegramUpdateHandler
        );
        return application;
    }
}