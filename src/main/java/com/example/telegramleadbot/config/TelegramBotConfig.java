package com.example.telegramleadbot.config;

import com.example.telegramleadbot.telegram.TelegramUpdateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
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
        try {
            log.info("Starting Telegram bot registration");

            TelegramBotsLongPollingApplication application = new TelegramBotsLongPollingApplication();
            application.registerBot(
                    telegramBotProperties.getToken(),
                    telegramUpdateHandler
            );

            log.info("Telegram bot registration completed successfully");
            return application;

        } catch (TelegramApiException e) {
            log.error("""
                    TELEGRAM_STARTUP_FAILED
                    Не удалось подключиться к Telegram Bot API при старте приложения.
                    Проверьте:
                    - доступность Telegram с VPS
                    - сетевой маршрут / ограничения провайдера
                    - корректность BOT_TOKEN
                    """, e);
            throw e;
        } catch (Exception e) {
            log.error("""
                    TELEGRAM_STARTUP_FAILED
                    Неожиданная ошибка при регистрации Telegram-бота.
                    Проверьте сетевой доступ VPS и настройки приложения.
                    """, e);
            throw e;
        }
    }
}