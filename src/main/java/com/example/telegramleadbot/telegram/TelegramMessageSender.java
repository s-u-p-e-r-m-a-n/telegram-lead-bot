package com.example.telegramleadbot.telegram;

import com.example.telegramleadbot.entity.UserSessionEntity;
import com.example.telegramleadbot.entity.enums.ConversationStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramMessageSender {

    private final TelegramClient telegramClient;


    public void sendStartMessage(Long chatId) {
        try {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Здравствуйте! Я помогу оставить заявку. Нажмите кнопку ниже.")
                    .replyMarkup(
                            InlineKeyboardMarkup.builder()
                                    .keyboard(List.of(
                                            new InlineKeyboardRow(
                                                    InlineKeyboardButton.builder()
                                                            .text("📝 Оставить заявку")
                                                            .callbackData("start_request")
                                                            .build()
                                            )
                                    ))
                                    .build()
                    )
                    .build();

            telegramClient.execute(sendMessage);
            log.info("Start message sent. chatId={}", chatId);
        } catch (Exception e) {
            log.error("Failed to send start message. chatId={}", chatId, e);
        }
    }


    public void sendText(Long chatId, String text) {
        try {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(text)
                    .build();

            telegramClient.execute(sendMessage);
            log.info("Message sent. chatId={}", chatId);
        } catch (Exception e) {
            log.error("Failed to send message. chatId={}", chatId, e);
        }
    }

    /**
     * processing the button
     */
    public void sendRequestTypeMessage(Long chatId) {
        try {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Выберите категорию заявки:")
                    .replyMarkup(
                            InlineKeyboardMarkup.builder()
                                    .keyboard(List.of(
                                            new InlineKeyboardRow(
                                                    InlineKeyboardButton.builder()
                                                            .text("💬 Консультация")
                                                            .callbackData("request_type:CONSULTATION")
                                                            .build()
                                            ),
                                            new InlineKeyboardRow(
                                                    InlineKeyboardButton.builder()
                                                            .text("🛠 Исправление ошибки")
                                                            .callbackData("request_type:BUG_FIX")
                                                            .build()
                                            ),
                                            new InlineKeyboardRow(
                                                    InlineKeyboardButton.builder()
                                                            .text("⚙️ Доработка проекта")
                                                            .callbackData("request_type:PROJECT_IMPROVEMENT")
                                                            .build()
                                            ),
                                            new InlineKeyboardRow(
                                                    InlineKeyboardButton.builder()
                                                            .text("📌 Другое")
                                                            .callbackData("request_type:OTHER")
                                                            .build()
                                            )
                                    ))
                                    .build()
                    )
                    .build();

            telegramClient.execute(sendMessage);
            log.info("Request type message sent. chatId={}", chatId);
        } catch (Exception e) {
            log.error("Failed to send request type message. chatId={}", chatId, e);
        }
    }

    public void sendEnterNameMessage(Long chatId) {
        sendText(chatId, "Введите ваше имя:");
    }

    public void sendEnterPhoneMessage(Long chatId) {
        sendText(chatId, "Введите ваш телефон:");
    }

    public void sendResumeOrRestartMessage(Long chatId) {
        try {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("У вас есть незавершенная заявка. Продолжить заполнение или начать заново?")
                    .replyMarkup(
                            InlineKeyboardMarkup.builder()
                                    .keyboard(List.of(
                                            new InlineKeyboardRow(
                                                    InlineKeyboardButton.builder()
                                                            .text("▶️ Продолжить")
                                                            .callbackData("continue_request")
                                                            .build()
                                            ),
                                            new InlineKeyboardRow(
                                                    InlineKeyboardButton.builder()
                                                            .text("🔄 Начать заново")
                                                            .callbackData("restart_request")
                                                            .build()
                                            )
                                    ))
                                    .build()
                    )
                    .build();

            telegramClient.execute(sendMessage);
            log.info("Resume/restart message sent. chatId={}", chatId);
        } catch (Exception e) {
            log.error("Failed to send resume/restart message. chatId={}", chatId, e);
        }
    }

    public void sendCurrentStepMessage(Long chatId, ConversationStep step) {
        switch (step) {
            case WAITING_NAME -> sendEnterNameMessage(chatId);
            case WAITING_PHONE -> sendEnterPhoneMessage(chatId);
            case WAITING_DESCRIPTION -> sendText(chatId, "Напишите комментарий к заявке:");
            default -> sendStartMessage(chatId);
        }
    }

    public void sendEnterDescriptionMessage(Long chatId) {
        sendText(chatId, "Напишите комментарий к заявке:");
    }

    public void sendLeadCreatedMessage(Long chatId) {
        try {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Заявка отправлена. Спасибо!")
                    .replyMarkup(
                            InlineKeyboardMarkup.builder()
                                    .keyboard(List.of(
                                            new InlineKeyboardRow(
                                                    InlineKeyboardButton.builder()
                                                            .text("👀 Посмотреть demo-результат")
                                                            .callbackData("show_demo_result")
                                                            .build()
                                            )
                                    ))
                                    .build()
                    )
                    .build();

            telegramClient.execute(sendMessage);
            log.info("Lead created message sent. chatId={}", chatId);
        } catch (Exception e) {
            log.error("Failed to send lead created message. chatId={}", chatId, e);
        }
    }

    public void sendLeadNotificationToAdmin(Long adminChatId, UserSessionEntity session, String description) {
        String message = """
                Новая заявка:
                
                Тип: %s
                Имя: %s
                Телефон: %s
                Комментарий: %s
                Источник: %s
                """.formatted(
                session.getDraftRequestType().getDisplayName(),
                session.getDraftName(),
                session.getDraftPhone(),
                description,
                session.getSource()
                //session.getChatId()
        );

        sendText(adminChatId, message);
    }

    public void sendDemoResultMessage(Long chatId, UserSessionEntity session) {
        String message = """
                Demo-результат:
                
                Так заявка выглядит у администратора:
                
                Тип: %s
                Имя: %s
                Телефон: %s
                Комментарий: %s
                """.formatted(
                session.getDraftRequestType() != null
                        ? session.getDraftRequestType().getDisplayName()
                        : "Не указано",
                session.getDraftName(),
                session.getDraftPhone(),
                session.getDraftDescription()
        );

        sendText(chatId, message);
    }

    public void sendHelpMessage(Long chatId) {
        sendText(chatId, """
                Этот бот помогает быстро оставить заявку.
                
                Что можно сделать:
                /start — начать работу
                /help — помощь
                /stats — статистика для администратора
                /last_leads — последние заявки для администратора
                
                После отправки заявки можно посмотреть demo-результат — как заявка выглядит для администратора.
                """);
    }

    public void sendLastLeadsPage(Long chatId, String text, int currentPage, boolean hasNextPage) {
        try {
            SendMessage.SendMessageBuilder builder = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(text);

            if (hasNextPage) {
                builder.replyMarkup(
                        InlineKeyboardMarkup.builder()
                                .keyboard(List.of(
                                        new InlineKeyboardRow(
                                                InlineKeyboardButton.builder()
                                                        .text("➡️ Следующие")
                                                        .callbackData("leads_page:" + (currentPage + 1))
                                                        .build()
                                        )
                                ))
                                .build()
                );
            }

            SendMessage sendMessage = builder.build();

            telegramClient.execute(sendMessage);
            log.info("Last leads page sent. chatId={}, page={}, hasNextPage={}", chatId, currentPage, hasNextPage);
        } catch (Exception e) {
            log.error("Failed to send last leads page. chatId={}, page={}", chatId, currentPage, e);
        }
    }
}