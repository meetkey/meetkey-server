package com.meetkey.server.domain.badge.respository;

import com.meetkey.server.domain.badge.entity.Badge;
import com.meetkey.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    Optional<Badge> findByMember(Member member);
}
