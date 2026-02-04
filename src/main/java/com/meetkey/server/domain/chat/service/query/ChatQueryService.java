package com.meetkey.server.domain.chat.service.query;

import com.meetkey.server.domain.chat.converter.ChatConverter;
import com.meetkey.server.domain.chat.dto.response.ChatResDTO;
import com.meetkey.server.domain.chat.entity.ChatMessage;
import com.meetkey.server.domain.chat.entity.ChatRoom;
import com.meetkey.server.domain.chat.entity.ChatRoomMember;
import com.meetkey.server.domain.chat.exception.ChatErrorStatus;
import com.meetkey.server.domain.chat.repository.ChatMessageRepository;
import com.meetkey.server.domain.chat.repository.ChatRoomMemberRepository;
import com.meetkey.server.domain.chat.repository.ChatRoomRepository;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.repository.MemberRepository;
import com.meetkey.server.domain.chat.exception.ChatException;
import com.meetkey.server.global.apiPayload.exception.GeneralException;
import com.meetkey.server.global.apiPayload.status.CommonErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatQueryService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    // 채팅방 목록 조회
    public List<ChatResDTO.ChatPreviewRes> getChatRoomList(Long memberId){
        //TODO: 나중에 만들어질 MemberErrorCode로 바꾸기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(CommonErrorStatus._INTERNAL_SERVER_ERROR));

        // TODO: 안 읽은 거 다 계산해야됨
        List<ChatResDTO.ChatPreviewRes> list = chatRoomMemberRepository.findOppChatRooomMembersOrderByUpdatedAtDesc(memberId)
                .stream().map(ChatConverter::toChatPreviewRes)
                .toList();
        return list;
    }

    // 채팅방 상세 조회
    public ChatResDTO.ChatMessageListRes getChatMessageList(Long memberId, Long chatRoomId, Long cursorId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(CommonErrorStatus._INTERNAL_SERVER_ERROR));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorStatus.CHAT_ROOM_NOT_FOUND));
        ChatRoomMember oppenetChatRoomMember = chatRoomMemberRepository.findByMemberAndChatRoom(member, chatRoom)
                .orElseThrow(() -> new ChatException(ChatErrorStatus.CHAT_ROOM_MEMBER_NOT_FOUND));

        // 값 받게끔 수정
        Pageable pageable = PageRequest.of(0, 30);

        Slice<ChatMessage> slice = (cursorId == null)
                ? chatMessageRepository.findByChatRoomOrderByIdDesc(chatRoom, pageable)
                : chatMessageRepository.findByChatRoomAndIdLessThanOrderByIdDesc(chatRoom, cursorId, pageable);

        List<ChatMessage> content = slice.getContent();

        Long nextCursor = content.isEmpty()
                ? null
                : content.get(content.size() - 1).getId();

        return ChatConverter.toChatMessageListRes(oppenetChatRoomMember, content, nextCursor, slice.hasNext());
    }
}
