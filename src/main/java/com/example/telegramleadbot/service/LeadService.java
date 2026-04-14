package com.example.telegramleadbot.service;

import com.example.telegramleadbot.entity.LeadEntity;
import com.example.telegramleadbot.entity.UserSessionEntity;
import com.example.telegramleadbot.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeadService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final LeadRepository leadRepository;

    public void createLead(UserSessionEntity session, String description) {
        LeadEntity lead = new LeadEntity();
        lead.setChatId(session.getChatId());
        lead.setSource(session.getSource());
        lead.setRequestType(session.getDraftRequestType());
        lead.setName(session.getDraftName());
        lead.setPhone(session.getDraftPhone());
        lead.setDescription(description);
        lead.setCreatedAt(LocalDateTime.now());

        leadRepository.save(lead);
    }

    public String buildLastLeadsMessage() {
        List<LeadEntity> leads = leadRepository.findTop5ByOrderByCreatedAtDesc();

        if (leads.isEmpty()) {
            return "Заявок пока нет.";
        }

        StringBuilder sb = new StringBuilder("Последние заявки:\n\n");

        for (LeadEntity lead : leads) {
            sb.append("Услуга: ").append(lead.getRequestType().getDisplayName()).append("\n")
                    .append("Имя: ").append(lead.getName()).append("\n")
                    .append("Телефон: ").append(lead.getPhone()).append("\n")
                    .append("Комментарий: ").append(lead.getDescription()).append("\n")
                    .append("Дата: ").append(lead.getCreatedAt().format(DATE_TIME_FORMATTER)).append("\n\n");
        }

        return sb.toString();
    }

}