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

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Builder.Default
    private boolean isVerified = false;

    private String location;

    private String bio;

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

    @Column(nullable = false)
    @Builder.Default
    private Integer recommendCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer notRecommendCount = 0;

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

    // 프로필 업데이트
    public void updateProfileInfo(String location, String bio, Language first, Language target, Level level) {
        this.location = location;
        this.bio = bio;
        this.firstLanguage = first;
        this.targetLanguage = target;
        this.targetLanguageLevel = level;
    }

    // 나이 계산 로직
    public int getAge() {
        if (this.birthday == null) return 0;
        return LocalDate.now().getYear() - this.birthday.getYear() + 1;
    }

    public void increaseRecommend() {
        this.recommendCount++;
    }

    public void decreaseRecommend() {
        if (this.recommendCount > 0) this.recommendCount--;
    }

    public void increaseNotRecommend() {
        this.notRecommendCount++;
    }

    public void decreaseNotRecommend() {
        if (this.notRecommendCount > 0) this.notRecommendCount--;
    }

    public void updateCertificated() {
        this.isVerified = true;
    }

}
