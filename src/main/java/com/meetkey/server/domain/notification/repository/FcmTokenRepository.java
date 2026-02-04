package com.meetkey.server.domain.notification.repository;

import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.entity.mapping.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    List<FcmToken> findAllByMember(Member member);

    boolean existsByMemberAndToken(Member member, String fcmToken);
}
