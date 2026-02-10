package com.meetkey.server.domain.mission.entity;

import com.meetkey.server.domain.mission.enums.MissionType;
import com.meetkey.server.global.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Table(name = "mission")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Mission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private MissionType missionType;

}
