package com.meetkey.server.domain.member.repository;

import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.entity.mapping.FromToId;
import com.meetkey.server.domain.member.entity.mapping.MemberLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberLikeRepository extends JpaRepository<MemberLike, FromToId> {
    @Query("SELECT ml.toMember.id FROM MemberLike ml WHERE ml.fromMember = :member")
    List<Long> findSwipedMemberIdsByMember(@Param("member") Member member);
}
