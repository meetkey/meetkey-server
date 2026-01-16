package com.meetkey.server.domain.member.entity;

import com.meetkey.server.domain.member.enums.*;
import com.meetkey.server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Table(name = "member")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Column(nullable = false)
    @Builder.Default
    private boolean isVerified = false;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private HomeTown homeTown;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Language firstLanguage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Language targetLanguage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Level targetLanguageLevel;

    @Column(unique = true)
    private String fcmToken;

    private LocalDateTime inactiveDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.INACTIVE;

    @Column(nullable = false, unique = true, length = 20)
    private String phoneNumber;
}
