package com.meetkey.server.domain.member.entity.mapping;

import com.meetkey.server.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "member_location")
public class MemberLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public static MemberLocation create(Member member, Double latitude, Double longitude) {
        return MemberLocation.builder()
                .member(member)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    public void update(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
