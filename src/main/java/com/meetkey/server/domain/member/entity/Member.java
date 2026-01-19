package com.meetkey.server.domain.member.entity;

import com.meetkey.server.domain.member.entity.mapping.InterestMember;
import com.meetkey.server.domain.member.enums.*;
import com.meetkey.server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Builder.Default
    private boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    private HomeTown homeTown;

    @Enumerated(EnumType.STRING)
    private Language firstLanguage;

    @Enumerated(EnumType.STRING)
    private Language targetLanguage;

    @Enumerated(EnumType.STRING)
    private Level targetLanguageLevel;

    private LocalDateTime inactiveDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.ACTIVE;

    @Column(unique = true, length = 20)
    private String phoneNumber;

    private String refreshToken;
    private LocalDateTime refreshTokenExpiration;

    /*
     * 관심사
     */
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<InterestMember> interestMembers = new ArrayList<>();


    public void changeRefreshToken(String newRefreshToken, Long expMillis) {
        this.refreshToken = newRefreshToken;
        this.refreshTokenExpiration = LocalDateTime.now().plusNanos(expMillis);
    }
}
