package com.meetkey.server.domain.chat.repository;

import com.meetkey.server.domain.chat.entity.ChatRoom;
import com.meetkey.server.domain.chat.entity.ChatRoomMember;
import com.meetkey.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

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

    boolean existsByChatRoomIdAndMemberId(Long chatRoomId, Long memberId);

    Optional<ChatRoomMember> findByChatRoomAndMemberNot(ChatRoom chatRoom, Member member);

    @Query("""
        select crm.member.id
        from ChatRoomMember crm
        where crm.chatRoom.id = :chatRoomId
    """)
    List<Long> findMemberIdsByChatRoomId(Long chatRoomId);

    @Query("""
        select crm2.member.id
        from ChatRoomMember crm1
        join ChatRoomMember crm2 on crm1.chatRoom = crm2.chatRoom
        where crm1.member = :member
        and crm2.member != :member
    """)
    List<Long> findChattedMemberIds(Member member);

}
