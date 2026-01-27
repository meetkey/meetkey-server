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
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatType chatType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FriendType friendType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RelationType relationType;

    public Preference(Member member, SocialType social, MeetingType meeting, ChatType chat, FriendType friend, RelationType relation) {
        this.member = member;
        this.socialType = social;
        this.meetingType = meeting;
        this.chatType = chat;
        this.friendType = friend;
        this.relationType = relation;
    }

    // 성향이 없는 경우 생성
    public static Preference create(Member member, SocialType social, MeetingType meeting, ChatType chat, FriendType friend, RelationType relation) {
        return new Preference(member, social, meeting, chat, friend, relation);
    }

    // 성향 수정 메서드
    public void update(SocialType social, MeetingType meeting, ChatType chat, FriendType friend, RelationType relation) {
        this.socialType = social;
        this.meetingType = meeting;
        this.chatType = chat;
        this.friendType = friend;
        this.relationType = relation;
    }


}
