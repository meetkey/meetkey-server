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
import com.meetkey.server.domain.member.exception.MemberErrorStatus;
import com.meetkey.server.domain.member.exception.MemberException;
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

import java.util.Collections;
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
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

        return chatRoomMemberRepository
                .findOppChatRooomMembersOrderByUpdatedAtDesc(memberId) // 상대 기준
                .stream()
                .map(opponentChatRoomMember -> {
                    ChatRoom chatRoom = opponentChatRoomMember.getChatRoom();
                    ChatRoomMember myChatRoomMember = chatRoomMemberRepository.findByMemberAndChatRoom(member, chatRoom)
                                    .orElseThrow(() -> new ChatException(ChatErrorStatus.CHAT_ROOM_MEMBER_NOT_FOUND));

                    // 미리보리용 최신 메시지
                    ChatMessage lastMessage = chatMessageRepository.findTop1ByChatRoomOrderByIdDesc(chatRoom).orElse(null);

                    // myChatRoomMember의 unreadCount 계산
                    ChatMessage lastReadMsg = myChatRoomMember.getLastReadMsg();

                    long unreadCount = (lastReadMsg == null)
                            ? chatMessageRepository.countByChatRoom(chatRoom)
                            : chatMessageRepository.countByChatRoomAndIdGreaterThan(chatRoom, lastReadMsg.getId());

                    return ChatConverter.toChatPreviewRes(
                            opponentChatRoomMember,
                            lastMessage,
                            unreadCount
                    );
                })
                .toList();
    }

    // 채팅방 상세 조회
    public ChatResDTO.ChatMessageListRes getChatMessageList(Long memberId, Long chatRoomId, Long cursorId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorStatus.CHAT_ROOM_NOT_FOUND));

        ChatRoomMember myChatRoomMember = chatRoomMemberRepository.findByMemberAndChatRoom(member, chatRoom)
                .orElseThrow(() -> new ChatException(ChatErrorStatus.CHAT_ROOM_NOT_FOUND));
        ChatRoomMember opponentChatRoomMember = chatRoomMemberRepository.findByChatRoomAndMemberNot(chatRoom, member)
                .orElseThrow(() -> new ChatException(ChatErrorStatus.CHAT_ROOM_MEMBER_NOT_FOUND));

        
        Pageable pageable = PageRequest.of(0, 30);

        Slice<ChatMessage> slice = (cursorId == null)
                ? chatMessageRepository.findByChatRoomOrderByIdAsc(chatRoom, pageable)
                : chatMessageRepository.findByChatRoomAndIdLessThanOrderByIdAsc(chatRoom, cursorId, pageable);

        List<ChatMessage> content = slice.getContent();

        Long nextCursor = content.isEmpty()
                ? null
                : content.get(content.size() - 1).getId();

        return ChatConverter.toChatMessageListRes(opponentChatRoomMember, content, nextCursor, slice.hasNext());
    }
}
