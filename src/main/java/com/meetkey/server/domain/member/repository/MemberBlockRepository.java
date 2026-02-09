package com.meetkey.server.domain.member.repository;

import com.meetkey.server.domain.member.entity.mapping.FromToId;
import com.meetkey.server.domain.member.entity.mapping.MemberBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberBlockRepository extends JpaRepository<MemberBlock, FromToId> {
    // fromId가 toId를 차단했는지를 알 수 있는 메소드
    // ex) existsByMemberBlockIdFromIdAndMemberBlockIdToId(myId, targetId)
    boolean existsByMemberBlockIdFromIdAndMemberBlockIdToId(Long fromId, Long toId);

    // 내가 누구를 차단했는지
    // ex)  List<MemberBlock> l = memberBlockRepository.findAllByMemberBlockIdFromId(1L);
    // for (MemberBlock mb : l){
    //            System.out.println(mb.getToMember().getId());
    // }
    List<MemberBlock> findAllByMemberBlockIdFromId(Long fromId);
}