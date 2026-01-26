package com.meetkey.server.domain.member.repository;

import com.meetkey.server.domain.member.entity.Interest;
import com.meetkey.server.domain.member.enums.InterestType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Long> {

    List<Interest> findAllByTypeIn(List<InterestType> interestNames);
}
