package com.meetkey.server.domain.member.repository;

import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.entity.mapping.InterestMember;
import com.meetkey.server.domain.member.enums.InterestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterestMemberRepository extends JpaRepository<InterestMember, Long> {

    @Modifying
    @Query("delete from InterestMember  im where im.member = :member")
    void deleteAllByMember(@Param("member") Member member);
}
