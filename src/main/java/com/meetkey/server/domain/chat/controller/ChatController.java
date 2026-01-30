package com.meetkey.server.domain.chat.controller;

import com.meetkey.server.domain.chat.dto.request.ChatReqDTO;
import com.meetkey.server.domain.chat.dto.response.ChatResDTO;
import com.meetkey.server.domain.chat.service.command.ChatCommandService;
import com.meetkey.server.domain.chat.service.query.ChatQueryService;
import com.meetkey.server.global.apiPayload.response.BasicResponse;
import com.meetkey.server.global.apiPayload.status.CommonSuccessStatus;
import com.meetkey.server.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "채팅  API")
@RequestMapping("/chat-room")
public class ChatController {

    private final ChatCommandService chatCommandService;
    private final ChatQueryService chatQueryService;

    @Operation(summary = "채팅방 생성 API by 제인", description = "채팅방을 생성하는 API by 제인")
    @PostMapping
    public BasicResponse<ChatResDTO.CreateChatRoomRes> createChatRoom(
            @RequestBody ChatReqDTO.CreateChatRoomReq req
            ,@AuthenticationPrincipal CustomUserDetails details
    ){
        ChatResDTO.CreateChatRoomRes chatRoom = chatCommandService.createChatRoom(req, details.getMemberId());
        return BasicResponse.success(CommonSuccessStatus._OK, chatRoom);
    }

    @Operation(summary = "채팅방 나가기 API by 제인", description = "채팅방을 생성하는 API by 제인")
    @DeleteMapping("/{chatRoomId}")
    public BasicResponse<Void> deleteChatRoom(
            @AuthenticationPrincipal CustomUserDetails details
            ,@PathVariable("chatRoomId") Long chatRoomId
    ){
        chatCommandService.deleteChatRoom(details.getMemberId(), chatRoomId);
        return BasicResponse.success(CommonSuccessStatus._OK, null);
    }

    @Operation(summary = "채팅방 목록 조회 API by 제인", description = "채팅방을 생성하는 API by 제인")
    @GetMapping
    public BasicResponse<List<ChatResDTO.ChatPreviewRes>> findChatRoomList(
            @AuthenticationPrincipal CustomUserDetails details
    ){
        List<ChatResDTO.ChatPreviewRes> chatRoomList = chatQueryService.getChatRoomList(details.getMemberId());
        return BasicResponse.success(CommonSuccessStatus._OK, chatRoomList);
    }

    @Operation(summary = "채팅방 상세 조회 API by 제인", description = "채팅방을 생성하는 API by 제인")
    @GetMapping("/{chatRoomId}/messages")
    public BasicResponse<ChatResDTO.ChatMessageListRes> findChatRoom(
            @PathVariable Long chatRoomId
            ,@RequestParam Long cursorId
            ,@AuthenticationPrincipal CustomUserDetails details
    ){
        ChatResDTO.ChatMessageListRes chatMessageList = chatQueryService.getChatMessageList(details.getMemberId(), chatRoomId, cursorId);
        return BasicResponse.success(CommonSuccessStatus._OK, chatMessageList);
    }

    @Operation(summary = "채팅방 메세지 읽음 처리 API by 제인", description = "채팅방을 생성하는 API by 제인")
    @PatchMapping("/{chatRoomId}/read")
    public BasicResponse<Void> findChatRoom(
            @PathVariable Long chatRoomId
            ,@AuthenticationPrincipal CustomUserDetails details
    ) {
        chatCommandService.readMessages(details.getMemberId(), chatRoomId);
        return BasicResponse.success(CommonSuccessStatus._OK, null);
    }
}
