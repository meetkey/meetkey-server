package com.meetkey.server.domain.member.repository;

import com.meetkey.server.domain.member.entity.Preference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {
}
