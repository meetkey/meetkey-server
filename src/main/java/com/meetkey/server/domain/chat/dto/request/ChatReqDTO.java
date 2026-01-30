package com.meetkey.server.domain.chat.dto.request;

import jakarta.validation.constraints.NotNull;

public class ChatReqDTO {

    public record CreateChatRoomReq(
            @NotNull
            Long targetUserId
    ){}
}
