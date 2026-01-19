package com.meetkey.server.domain.member.entity;

import com.meetkey.server.domain.member.enums.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "preference")
public class Preference {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private SocialType socialType;

    @Column(nullable = false)
    private MeetingType meetingType;

    @Column(nullable = false)
    private ChatType chatType;

    @Column(nullable = false)
    private FriendType friendType;

    @Column(nullable = false)
    private RelationType relationType;
}
