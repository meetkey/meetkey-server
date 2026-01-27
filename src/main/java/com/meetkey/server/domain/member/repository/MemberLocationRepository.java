package com.meetkey.server.domain.member.repository;

import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.entity.mapping.MemberLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberLocationRepository extends JpaRepository<MemberLocation, Long> {

    Optional<MemberLocation> findByMember(Member member);
}
