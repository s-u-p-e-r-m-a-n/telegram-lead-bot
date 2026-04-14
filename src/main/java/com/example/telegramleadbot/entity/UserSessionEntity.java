package com.example.telegramleadbot.entity;

import com.example.telegramleadbot.entity.enums.ConversationStep;
import com.example.telegramleadbot.entity.enums.LeadSource;
import com.example.telegramleadbot.entity.enums.RequestType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
@Getter
@Setter
public class UserSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", nullable = false, unique = true)
    private Long chatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private LeadSource source;

    @Enumerated(EnumType.STRING)
    @Column(name = "step", nullable = false)
    private ConversationStep step;

    @Enumerated(EnumType.STRING)
    @Column(name = "draft_request_type")
    private RequestType draftRequestType;

    @Column(name = "draft_name")
    private String draftName;

    @Column(name = "draft_phone")
    private String draftPhone;

    @Column(name = "draft_description", length = 2000)
    private String draftDescription;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}