package com.meetkey.server.domain.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class ChatReqDTO {

    public record CreateChatRoomReq(
            @NotNull
            Long targetUserId
    ){}

    public record ChatAlarmReq(
            @Schema(description = "알림 켜기(true) / 끄기(false)", example = "false")
            boolean isAlarm
    ) {}
}
