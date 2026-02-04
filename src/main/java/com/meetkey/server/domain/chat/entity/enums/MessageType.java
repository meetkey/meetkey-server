package com.meetkey.server.domain.chat.entity.enums;

public enum MessageType {
    TEXT(false),
    IMAGE(false),
    VOICE(true);

    private final boolean requiresDuration;

    MessageType(boolean requiresDuration) {
        this.requiresDuration = requiresDuration;
    }

    public boolean requiresDuration() {
        return requiresDuration;
    }
}
