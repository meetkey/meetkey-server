package com.meetkey.server.domain.mission.service;

import com.meetkey.server.domain.badge.entity.Badge;
import com.meetkey.server.domain.badge.enums.ReasonType;
import com.meetkey.server.domain.badge.respository.BadgeRepository;
import com.meetkey.server.domain.badge.respository.PointHistoryRepository;
import com.meetkey.server.domain.badge.service.BadgeService;
import com.meetkey.server.domain.chat.entity.ChatRoom;
import com.meetkey.server.domain.chat.exception.ChatErrorStatus;
import com.meetkey.server.domain.chat.exception.ChatException;
import com.meetkey.server.domain.chat.repository.ChatRoomRepository;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.exception.MemberErrorStatus;
import com.meetkey.server.domain.member.exception.MemberException;
import com.meetkey.server.domain.member.repository.MemberRepository;
import com.meetkey.server.domain.mission.converter.MissionConverter;
import com.meetkey.server.domain.mission.entity.Mission;
import com.meetkey.server.domain.mission.entity.mapping.ChatRoomMission;
import com.meetkey.server.domain.mission.entity.mapping.MissionLog;
import com.meetkey.server.domain.mission.enums.MissionStatus;
import com.meetkey.server.domain.mission.exception.MissionErrorStatus;
import com.meetkey.server.domain.mission.exception.MissionException;
import com.meetkey.server.domain.mission.respository.ChatRoomMissionRepository;
import com.meetkey.server.domain.mission.respository.MissionLogRepository;
import com.meetkey.server.domain.mission.respository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.meetkey.server.domain.mission.dto.MissionResDTO.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MissionService {

    private final MissionRepository missionRepository;
    private final ChatRoomMissionRepository chatRoomMissionRepository;
    private final MissionLogRepository missionLogRepository;
    private final MemberRepository memberRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final ChatRoomRepository chatRoomRepository;

    private final MissionConverter missionConverter;

    private final BadgeService badgeService;
    private final BadgeRepository badgeRepository;

    public Info getTodayMission(Long chatRoomId, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new ChatException(ChatErrorStatus.CHAT_ROOM_NOT_FOUND));
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

        // 미션 가져오기 (없으면 생성 시도)
        ChatRoomMission currentMission = getOrCreateDailyMission(chatRoom);

        // 내 참여 로그 가져오기 (없으면 생성)
        MissionLog myLog = getOrCreateMissionLog(currentMission, member);

        // 남은 시간 계산 및 반환
        long remainingSeconds = calculateRemainingSeconds(currentMission.getExpiresAt());

        return missionConverter.toMissionInfo(currentMission, myLog, remainingSeconds);
    }



    // 미션 완료 처리
    public Completion completeMission(Long chatRoomId, Long missionId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

        ChatRoomMission chatRoomMission = chatRoomMissionRepository.findById(missionId).orElseThrow(
                () -> new MissionException(MissionErrorStatus.MISSION_NOT_FOUND));

        if (chatRoomMission.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new MissionException(MissionErrorStatus.ALREADY_CLEAR);
        }

        MissionLog log = missionLogRepository.findByChatRoomMissionAndMember(chatRoomMission, member)
                .orElseThrow(() -> new MissionException(MissionErrorStatus.PARTICIPATION_NOT_FOUND));

        if (log.getMissionStatus() == MissionStatus.SUCCESS) {
            throw new MissionException(MissionErrorStatus.ALREADY_CLEAR);
        }

        // 상태 변경 및 점수 지급
        log.complete();

        badgeService.rewardPoints(member, ReasonType.MISSION_SUCCESS);
        Badge badge = badgeRepository.findByMember(member).orElseThrow();
        return missionConverter.toMissionCompletion(badge, ReasonType.MISSION_SUCCESS);

    }

    public int processExpiredMission() {
        LocalDateTime now = LocalDateTime.now();
        List<MissionLog> expiredLogs = missionLogRepository.findAllExpiredPendingLogs(now);

        int count = 0;
        for (MissionLog log : expiredLogs) {
            log.fail();
            badgeService.rewardPoints(log.getMember(), ReasonType.MISSION_FAILURE);

            count ++;
        }

        return count;
    }


    private ChatRoomMission getOrCreateDailyMission(ChatRoom chatRoom) {
        Optional<ChatRoomMission> missionOpt = chatRoomMissionRepository
                .findFirstByChatRoomOrderByAssignedAtDesc(chatRoom);

        // 미션이 존재하고, 만료되지 않았으면 리턴
        if (missionOpt.isPresent() && missionOpt.get().getExpiresAt().isAfter(LocalDateTime.now())) {
            return missionOpt.get();
        }

        // 없거나 만료됐으면 -> 새로 생성
        try {
            return createNewMission(chatRoom);
        } catch (DataIntegrityViolationException e) {
            return chatRoomMissionRepository.findFirstByChatRoomOrderByAssignedAtDesc(chatRoom)
                    .orElseThrow(() -> new MissionException(MissionErrorStatus.MISSION_NOT_FOUND));
        }
    }

    private MissionLog getOrCreateMissionLog(ChatRoomMission mission, Member member) {
        return missionLogRepository.findByChatRoomMissionAndMember(mission, member)
                .orElseGet(() -> {
                    // 로그 생성 로직
                    return createPendingLog(mission, member);
                });
    }

    private long calculateRemainingSeconds(LocalDateTime expiresAt) {
        long seconds = Duration.between(LocalDateTime.now(), expiresAt).getSeconds();
        return seconds < 0 ? 0 : seconds;
    }

    // 새로운 미션 할당 로직
    private ChatRoomMission createNewMission(ChatRoom chatRoom) {
        Mission randomMission = missionRepository.findRandomMission()
                .orElseThrow(() -> new MissionException(MissionErrorStatus.MISSION_NOT_FOUND));

        ChatRoomMission newAssignment = ChatRoomMission.builder()
                .chatRoom(chatRoom)
                .mission(randomMission)
                .assignedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        return chatRoomMissionRepository.save(newAssignment);
    }

    // 멤버별 로그 생성 로직
    private MissionLog createPendingLog(ChatRoomMission mission, Member member) {
        MissionLog log = MissionLog.builder()
                .chatRoomMission(mission)
                .member(member)
                .missionStatus(MissionStatus.PENDING)
                .build();
        return missionLogRepository.save(log);
    }
}
