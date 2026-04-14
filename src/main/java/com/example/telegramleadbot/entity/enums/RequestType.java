package com.example.telegramleadbot.entity.enums;

public enum RequestType {
    CONSULTATION("Консультация"),
    BUG_FIX("Исправление ошибки"),
    PROJECT_IMPROVEMENT("Доработка проекта"),
    OTHER("Другое");

    private final String displayName;

    RequestType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}