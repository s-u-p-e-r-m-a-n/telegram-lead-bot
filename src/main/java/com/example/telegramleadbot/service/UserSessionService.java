package com.example.telegramleadbot.service;

import com.example.telegramleadbot.entity.UserSessionEntity;
import com.example.telegramleadbot.entity.enums.ConversationStep;
import com.example.telegramleadbot.entity.enums.LeadSource;
import com.example.telegramleadbot.entity.enums.RequestType;
import com.example.telegramleadbot.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSessionService {

    private final UserSessionRepository userSessionRepository;

    public void saveRequestType(Long chatId, LeadSource source, RequestType requestType) {
        UserSessionEntity session = userSessionRepository.findByChatId(chatId)
                .orElseGet(UserSessionEntity::new);

        session.setChatId(chatId);
        session.setSource(source);
        session.setDraftRequestType(requestType);
        session.setStep(ConversationStep.WAITING_NAME);
        session.setUpdatedAt(LocalDateTime.now());

        userSessionRepository.save(session);
    }

    public void saveName(Long chatId, String name) {
        UserSessionEntity session = userSessionRepository.findByChatId(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found for chatId=" + chatId));

        session.setDraftName(name);
        session.setStep(ConversationStep.WAITING_PHONE);
        session.setUpdatedAt(LocalDateTime.now());

        userSessionRepository.save(session);
    }

    public Optional<UserSessionEntity> findByChatId(Long chatId) {
        return userSessionRepository.findByChatId(chatId);
    }

    public boolean hasActiveSession(Long chatId) {
        return userSessionRepository.findByChatId(chatId)
                .filter(session -> session.getStep() != ConversationStep.COMPLETED)
                .isPresent();
    }

    public void restartSession(Long chatId) {
        UserSessionEntity session = userSessionRepository.findByChatId(chatId)
                .orElseGet(UserSessionEntity::new);

        LeadSource source = session.getSource() != null ? session.getSource() : LeadSource.UNKNOWN;

        session.setChatId(chatId);
        session.setSource(source);
        session.setDraftRequestType(null);
        session.setDraftName(null);
        session.setDraftPhone(null);
        session.setStep(ConversationStep.START);
        session.setUpdatedAt(LocalDateTime.now());

        userSessionRepository.save(session);
    }

    public UserSessionEntity getByChatId(Long chatId) {
        return userSessionRepository.findByChatId(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found for chatId=" + chatId));
    }

    public void savePhone(Long chatId, String phone) {
        UserSessionEntity session = userSessionRepository.findByChatId(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found for chatId=" + chatId));

        session.setDraftPhone(phone);
        session.setStep(ConversationStep.WAITING_DESCRIPTION);
        session.setUpdatedAt(LocalDateTime.now());

        userSessionRepository.save(session);
    }

    public void completeSession(Long chatId, String description) {
        UserSessionEntity session = userSessionRepository.findByChatId(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found for chatId=" + chatId));

        session.setDraftDescription(description);
        session.setStep(ConversationStep.COMPLETED);
        session.setUpdatedAt(LocalDateTime.now());

        userSessionRepository.save(session);
    }

    public void saveSource(Long chatId, LeadSource source) {
        UserSessionEntity session = userSessionRepository.findByChatId(chatId)
                .orElseGet(UserSessionEntity::new);

        session.setChatId(chatId);
        session.setSource(source);

        if (session.getStep() == null) {
            session.setStep(ConversationStep.START);
        }

        session.setUpdatedAt(LocalDateTime.now());
        userSessionRepository.save(session);
    }
}