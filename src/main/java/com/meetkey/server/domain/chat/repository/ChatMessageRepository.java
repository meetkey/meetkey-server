package com.meetkey.server.domain.chat.repository;

import com.meetkey.server.domain.chat.entity.ChatMessage;
import com.meetkey.server.domain.chat.entity.ChatRoom;
import com.meetkey.server.domain.chat.entity.enums.MessageType;
import com.meetkey.server.domain.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Slice<ChatMessage> findByChatRoomOrderByIdAsc(ChatRoom chatRoom, Pageable pageable);

    Slice<ChatMessage> findByChatRoomAndIdLessThanOrderByIdAsc(
            ChatRoom chatRoom,
            Long cursorId,
            Pageable pageable
    );

    Optional<ChatMessage> findTop1ByChatRoomOrderByIdDesc(ChatRoom chatRoom);

    long countByChatRoom(ChatRoom chatRoom);

    long countByChatRoomAndIdGreaterThan(ChatRoom chatRoom, Long messageId);

    // 해당 채팅방에서 특정 시간 이후에 특정 타입의 메시지를 보낸적 있는지 판별
    boolean existsByChatRoomAndMemberAndCreatedAtAfterAndMessageType(
            ChatRoom chatRoom,
            Member sender,
            LocalDateTime timestamp,
            MessageType messageType
    );
}
