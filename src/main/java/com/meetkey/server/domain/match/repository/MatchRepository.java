package com.meetkey.server.domain.match.repository;

import com.meetkey.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Member, Long>, MatchRepositoryCustom {
}
