package com.example.telegramleadbot.unit.service;

import com.example.telegramleadbot.entity.LeadEntity;
import com.example.telegramleadbot.entity.UserSessionEntity;
import com.example.telegramleadbot.entity.enums.ConversationStep;
import com.example.telegramleadbot.entity.enums.LeadSource;
import com.example.telegramleadbot.entity.enums.RequestType;
import com.example.telegramleadbot.repository.LeadRepository;
import com.example.telegramleadbot.service.LeadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;
    @InjectMocks
    private LeadService leadService;


    @Test
    @DisplayName("Успешное сохранение заявки")
    void testSuccessfulSavingOfTheApplication() {
        String description = "Тестовое описание текста заявки";
        UserSessionEntity userSessionEntity = new UserSessionEntity();
        userSessionEntity.setChatId(24524L);
        userSessionEntity.setUpdatedAt(LocalDateTime.now());
        userSessionEntity.setSource(LeadSource.UNKNOWN);
        userSessionEntity.setDraftRequestType(RequestType.CONSULTATION);
        userSessionEntity.setDraftName("Vova");
        userSessionEntity.setDraftPhone("89304567645");
        userSessionEntity.setStep(ConversationStep.WAITING_DESCRIPTION);

        leadService.createLead(userSessionEntity, description);
        ArgumentCaptor<LeadEntity> captor = ArgumentCaptor.forClass(LeadEntity.class);
        verify(leadRepository).save(captor.capture());

        LeadEntity savedLead = captor.getValue();

        assertEquals("Vova", savedLead.getName());
        assertEquals("89304567645", savedLead.getPhone());
        assertEquals(description, savedLead.getDescription());
        assertEquals(RequestType.CONSULTATION, savedLead.getRequestType());
        assertEquals(24524L, savedLead.getChatId());


    }


}
