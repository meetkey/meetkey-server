package com.meetkey.server.domain.member.repository;

import com.meetkey.server.domain.member.entity.SocialLogin;
import com.meetkey.server.domain.member.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SocialLoginRepository extends JpaRepository<SocialLogin, Long> {
    @Query("SELECT s FROM SocialLogin s JOIN FETCH s.member " +
            "WHERE s.provider = :provider AND s.providerId = :providerId")
    Optional<SocialLogin> findByProviderAndProviderId(
            @Param("provider") Provider provider, @Param("providerId") String providerId);
}
