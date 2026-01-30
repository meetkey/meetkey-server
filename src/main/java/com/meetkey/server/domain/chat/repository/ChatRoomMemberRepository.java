package com.meetkey.server.domain.chat.repository;

import com.meetkey.server.domain.chat.entity.ChatRoom;
import com.meetkey.server.domain.chat.entity.ChatRoomMember;
import com.meetkey.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    List<ChatRoomMember> findByMemberOrderByUpdatedAtDesc(Member member);

    @Query("""
    select crm
    from ChatRoomMember crm
    where crm.chatRoom.id in (
        select crm2.chatRoom.id
        from ChatRoomMember crm2
        where crm2.member.id = :myMemberId
    )
    and crm.member.id != :myMemberId
    order by crm.updatedAt desc
    """)
    List<ChatRoomMember> findOppChatRooomMembersOrderByUpdatedAtDesc(Long myMemberId);

    Optional<ChatRoomMember> findByMemberAndChatRoom(Member member, ChatRoom chatRoom);

    @Query("""
        select crm
        from ChatRoomMember crm
        where crm.chatRoom.id = :chatRoomId
            and crm.member.id != :memberId
    """)
    ChatRoomMember findOppenentChatRoomMember(Long chatRoomId, Long memberId);
}
