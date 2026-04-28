package com.example.telegramleadbot.unit.service;


import com.example.telegramleadbot.entity.UserSessionEntity;
import com.example.telegramleadbot.entity.enums.ConversationStep;
import com.example.telegramleadbot.entity.enums.LeadSource;
import com.example.telegramleadbot.repository.UserSessionRepository;
import com.example.telegramleadbot.service.UserSessionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSessionServiceTest {

    @Mock
    private UserSessionRepository userSessionRepository;
    @InjectMocks
    private UserSessionService userSessionService;

    @Test
    @DisplayName("успешное сохранение сессии имени")
    void successfulSessionSavingName() {

        Long chatId = 1L;
        String name = "test";
        UserSessionEntity session = new UserSessionEntity();
        session.setChatId(chatId);
        session.setSource(LeadSource.UNKNOWN);
        when(userSessionRepository.findByChatId(chatId)).thenReturn(Optional.of(session));

        userSessionService.saveName(chatId, name);

        ArgumentCaptor<UserSessionEntity> captor = ArgumentCaptor.forClass(UserSessionEntity.class);
        verify(userSessionRepository).save(captor.capture());
        UserSessionEntity userSessionEntity = captor.getValue();

        assertEquals(name, userSessionEntity.getDraftName());
        assertEquals(chatId, userSessionEntity.getChatId());
        assertEquals(ConversationStep.WAITING_PHONE, userSessionEntity.getStep());
        assertTrue(userSessionEntity.getUpdatedAt()!=null);


    }

}
