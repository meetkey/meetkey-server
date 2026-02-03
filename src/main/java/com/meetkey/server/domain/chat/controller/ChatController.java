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
@Tag(name = "채팅 API By 제인")
@RequestMapping("/chat-room")
public class ChatController {

    private final ChatCommandService chatCommandService;
    private final ChatQueryService chatQueryService;

    @Operation(summary = "채팅방 생성 API", description = "새로운 채팅방을 생성합니다. 상대방 ID 등을 전달받아 방을 개설합니다.")
    @PostMapping
    public BasicResponse<ChatResDTO.CreateChatRoomRes> createChatRoom(
            @RequestBody ChatReqDTO.CreateChatRoomReq req
            ,@AuthenticationPrincipal CustomUserDetails details
    ){
        ChatResDTO.CreateChatRoomRes chatRoom = chatCommandService.createChatRoom(req, details.getMemberId());
        return BasicResponse.success(CommonSuccessStatus._OK, chatRoom);
    }

    @Operation(summary = "채팅방 나가기 API", description = "특정 채팅방에서 나갑니다. 해당 유저는 더 이상 채팅 목록에 노출되지 않습니다.")
    @DeleteMapping("/{chatRoomId}")
    public BasicResponse<Void> deleteChatRoom(
            @AuthenticationPrincipal CustomUserDetails details
            ,@PathVariable("chatRoomId") Long chatRoomId
    ){
        chatCommandService.deleteChatRoom(details.getMemberId(), chatRoomId);
        return BasicResponse.success(CommonSuccessStatus._OK, null);
    }

    @Operation(summary = "채팅방 목록 조회 API", description = "로그인한 사용자가 참여 중인 모든 채팅방 목록을 최신순으로 조회합니다.")
    @GetMapping
    public BasicResponse<List<ChatResDTO.ChatPreviewRes>> findChatRoomList(
            @AuthenticationPrincipal CustomUserDetails details
    ){
        List<ChatResDTO.ChatPreviewRes> chatRoomList = chatQueryService.getChatRoomList(details.getMemberId());
        return BasicResponse.success(CommonSuccessStatus._OK, chatRoomList);
    }

    @Operation(summary = "채팅방 상세 조회 API", description = "특정 채팅방의 과거 메시지 내역을 조회합니다. cursorId를 이용한 무한 스크롤 방식을 지원합니다.")
    @GetMapping("/{chatRoomId}/messages")
    public BasicResponse<ChatResDTO.ChatMessageListRes> findChatRoom(
            @PathVariable Long chatRoomId
            ,@RequestParam(required = false) Long cursorId
            ,@AuthenticationPrincipal CustomUserDetails details
    ){
        ChatResDTO.ChatMessageListRes chatMessageList = chatQueryService.getChatMessageList(details.getMemberId(), chatRoomId, cursorId);
        return BasicResponse.success(CommonSuccessStatus._OK, chatMessageList);
    }

    @Operation(summary = "채팅방 메세지 읽음 처리 API", description = "특정 채팅방의 읽지 않은 모든 메시지를 읽음 상태로 업데이트합니다.")
    @PatchMapping("/{chatRoomId}/read")
    public BasicResponse<Void> findChatRoom(
            @PathVariable Long chatRoomId
            ,@AuthenticationPrincipal CustomUserDetails details
    ) {
        chatCommandService.readMessages(details.getMemberId(), chatRoomId);
        return BasicResponse.success(CommonSuccessStatus._OK, null);
    }
}
