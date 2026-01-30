package com.meetkey.server.domain.chat.repository;

import com.meetkey.server.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByDirectKey(String directKey);


}
