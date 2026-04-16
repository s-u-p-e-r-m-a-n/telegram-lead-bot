package com.example.telegramleadbot.telegram;

import com.example.telegramleadbot.config.TelegramBotProperties;
import com.example.telegramleadbot.entity.UserSessionEntity;
import com.example.telegramleadbot.entity.enums.BotEventType;
import com.example.telegramleadbot.entity.enums.ConversationStep;
import com.example.telegramleadbot.entity.enums.LeadSource;
import com.example.telegramleadbot.entity.enums.RequestType;
import com.example.telegramleadbot.service.BotEventService;
import com.example.telegramleadbot.service.LeadService;
import com.example.telegramleadbot.service.StatsService;
import com.example.telegramleadbot.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramUpdateHandler implements LongPollingUpdateConsumer {

    private final TelegramMessageSender telegramMessageSender;
    private final TelegramBotProperties telegramBotProperties;
    private final UserSessionService userSessionService;
    private final BotEventService botEventService;
    private final LeadService leadService;
    private final StatsService statsService;

    @Override
    public void consume(List<Update> updates) {
        for (Update update : updates) {
            handleUpdate(update);
        }
    }

    private void handleUpdate(Update update) {
        log.info("Получено обновление телеграмм");

        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();

            log.info("Входящее сообщение. chatId={}, text={}", chatId, text);
            if (text.startsWith("/start")) {
                log.info("Команда /start получена от chatId={}", chatId);

                if (userSessionService.hasActiveSession(chatId)) {
                    telegramMessageSender.sendResumeOrRestartMessage(chatId);
                    return;
                }

                LeadSource source = resolveSourceFromStartCommand(text);
                botEventService.saveEvent(chatId, source, BotEventType.START);
                userSessionService.saveSource(chatId, source);
                telegramMessageSender.sendStartMessage(chatId);
                return;
            }
            if ("/help".equals(text)) {
                telegramMessageSender.sendHelpMessage(chatId);
                return;
            }
            if ("/stats".equals(text)) {
                if (chatId.equals(telegramBotProperties.getAdminChatId())) {
                    telegramMessageSender.sendText(chatId, statsService.buildStatsMessage());
                }
                return;
            }

            if ("/last_leads".equals(text)) {
                if (chatId.equals(telegramBotProperties.getAdminChatId())) {
                    int page = 0;

                    telegramMessageSender.sendLastLeadsPage(
                            chatId,
                            leadService.buildLastLeadsMessage(page),
                            page,
                            leadService.hasNextLeadsPage(page)
                    );
                }
                return;
            }

            UserSessionEntity session = userSessionService.findByChatId(chatId).orElse(null);

            if (session != null && session.getStep() == ConversationStep.WAITING_NAME) {
                userSessionService.saveName(chatId, text);
                telegramMessageSender.sendEnterPhoneMessage(chatId);
                return;
            }

            if (session != null && session.getStep() == ConversationStep.WAITING_PHONE) {
                userSessionService.savePhone(chatId, text);
                telegramMessageSender.sendEnterDescriptionMessage(chatId);
                return;
            }
            if (session != null && session.getStep() == ConversationStep.WAITING_DESCRIPTION) {
                leadService.createLead(session, text);
                userSessionService.completeSession(chatId, text);

                telegramMessageSender.sendLeadCreatedMessage(chatId);
                telegramMessageSender.sendLeadNotificationToAdmin(
                        telegramBotProperties.getAdminChatId(),
                        session,
                        text
                );


                botEventService.saveEvent(chatId, session.getSource(), BotEventType.REQUEST_COMPLETED);

                return;
            }

        }

        if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String callbackData = update.getCallbackQuery().getData();

            log.info("Incoming callback. chatId={}, data={}", chatId, callbackData);

            if ("start_request".equals(callbackData)) {
                LeadSource source = userSessionService.findByChatId(chatId)
                        .map(UserSessionEntity::getSource)
                        .orElse(LeadSource.UNKNOWN);
                botEventService.saveEvent(chatId, source, BotEventType.REQUEST_STARTED);
                telegramMessageSender.sendRequestTypeMessage(chatId);
            }
            if (callbackData.startsWith("request_type:")) {
                String requestTypeValue = callbackData.substring("request_type:".length());
                RequestType requestType = RequestType.valueOf(requestTypeValue);

                LeadSource source = userSessionService.findByChatId(chatId)
                        .map(UserSessionEntity::getSource)
                        .orElse(LeadSource.UNKNOWN);

                userSessionService.saveRequestType(chatId, source, requestType);
                telegramMessageSender.sendEnterNameMessage(chatId);
            }
            if ("continue_request".equals(callbackData)) {
                UserSessionEntity session = userSessionService.getByChatId(chatId);
                telegramMessageSender.sendCurrentStepMessage(chatId, session.getStep());
                return;
            }

            if ("restart_request".equals(callbackData)) {
                userSessionService.restartSession(chatId);
                telegramMessageSender.sendStartMessage(chatId);
                return;
            }
            if ("show_demo_result".equals(callbackData)) {
                UserSessionEntity session = userSessionService.getByChatId(chatId);
                telegramMessageSender.sendDemoResultMessage(chatId, session);
                return;
            }
            /** Processing the following buttons */
            if (callbackData.startsWith("leads_page:")) {
                int page = Integer.parseInt(callbackData.substring("leads_page:".length()));

                telegramMessageSender.sendLastLeadsPage(
                        chatId,
                        leadService.buildLastLeadsMessage(page),
                        page,
                        leadService.hasNextLeadsPage(page)
                );
                return;
            }

            return;
        }
    }

    private LeadSource resolveSourceFromStartCommand(String text) {
        if (text == null) {
            return LeadSource.UNKNOWN;
        }

        String normalized = text.trim().toLowerCase();

        if (normalized.startsWith("/start fl")) {
            return LeadSource.FL;
        }

        if (normalized.startsWith("/start avito")) {
            return LeadSource.AVITO;
        }

        return LeadSource.UNKNOWN;
    }


}