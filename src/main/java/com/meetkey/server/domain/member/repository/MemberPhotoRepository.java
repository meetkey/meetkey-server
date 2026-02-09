package com.meetkey.server.domain.member.repository;

import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.entity.mapping.MemberPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberPhotoRepository extends JpaRepository<MemberPhoto, Long> {
    List<MemberPhoto> findAllByMember(Member member);
}
