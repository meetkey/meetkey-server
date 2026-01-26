package com.meetkey.server.domain.member.repository;

import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.entity.mapping.InterestMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InterestMemberRepository extends JpaRepository<InterestMember, Long> {

    @Modifying
    @Query("delete from InterestMember  im where im.member = :member")
    void deleteAllByMember(@Param("member") Member member);

    // interest 페치 조인
    @EntityGraph(attributePaths = "interest")
    List<InterestMember> findAllByMember(Member member);
}
