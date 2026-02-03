package com.meetkey.server.domain.chat.repository;

import com.meetkey.server.domain.chat.entity.ChatMessage;
import com.meetkey.server.domain.chat.entity.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

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

}
