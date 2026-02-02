package com.meetkey.server.domain.member.service;

import com.meetkey.server.domain.badge.service.BadgeService;
import com.meetkey.server.domain.member.converter.ProfileConverter;
import com.meetkey.server.domain.member.entity.Interest;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.entity.Preference;
import com.meetkey.server.domain.member.entity.mapping.Evaluation;
import com.meetkey.server.domain.member.entity.mapping.InterestMember;
import com.meetkey.server.domain.member.entity.mapping.MemberLocation;
import com.meetkey.server.domain.member.enums.EvaluationType;
import com.meetkey.server.domain.member.enums.InterestType;
import com.meetkey.server.domain.member.exception.MemberErrorStatus;
import com.meetkey.server.domain.member.exception.MemberException;
import com.meetkey.server.domain.member.repository.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.meetkey.server.domain.badge.dto.BadgeResDTO.BadgeResponse;
import static com.meetkey.server.domain.member.dto.ProfileReqDTO.*;
import static com.meetkey.server.domain.member.dto.ProfileResDTO.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final MemberRepository memberRepository;
    private final ProfileConverter profileConverter;
    private final InterestRepository interestRepository;
    private final InterestMemberRepository interestMemberRepository;
    private final PreferenceRepository preferenceRepository;
    private final MemberLocationRepository memberLocationRepository;
    private final EvaluationRepository evaluationRepository;
    private final BadgeService badgeService;

    public ProfileUpdateResponse updateProfile(Long memberId, ProfileUpdateRequest request) {
        Member member = getMember(memberId);

        member.updateProfileInfo(request.location(), request.bio(), request.first(), request.target(), request.level());

        if (request.latitude() != null && request.longitude() != null) {
            MemberLocation memberLocation = memberLocationRepository.findByMember(member)
                .orElse(null);

            if (memberLocation == null) {
                MemberLocation newMemberLocation = MemberLocation.create(member, request.latitude(), request.longitude());
                memberLocationRepository.save(newMemberLocation);
            } else {
                memberLocation.update(request.latitude(), request.longitude());
            }
        }

        badgeService.checkProfileCompletion(memberId);
        return profileConverter.toProfileUpdateResponse(member);
    }

    public void updateLocation(Long memberId, LocationUpdateRequest request) {
        Member member = getMember(memberId);

        if (request.latitude() != null && request.longitude() != null) {
            MemberLocation memberLocation = memberLocationRepository.findByMember(member)
                .orElse(null);

            if (memberLocation == null) {
                MemberLocation newMemberLocation = MemberLocation.create(member, request.latitude(), request.longitude());
                memberLocationRepository.save(newMemberLocation);
            } else {
                memberLocation.update(request.latitude(), request.longitude());
            }
        }
    }

    @Transactional(readOnly = true)
    public ProfileUpdateResponse getMyUpdateProfile(Long memberId) {
        Member member = getMember(memberId);

        return profileConverter.toProfileUpdateResponse(member);
    }

    public InterestResponse updateInterests(Long memberId, List<InterestType> interestNames) {
        Member member = getMember(memberId);

        // 기존 관심사 삭제
        interestMemberRepository.deleteAllByMember(member);

        // 요청받은 관심사 조회
        List<Interest> interests = interestRepository.findAllByTypeIn(interestNames);

        List<InterestMember> newMappings = interests.stream()
            .map(interest -> InterestMember.create(member, interest))
            .collect(Collectors.toList());

        interestMemberRepository.saveAll(newMappings);

        return profileConverter.toInterestResponse(interests);
    }

    @Transactional(readOnly = true)
    public InterestCategoryResponse getAllInterests() {
        return profileConverter.toCategoryResponse();
    }

    @Transactional(readOnly = true)
    public PersonalityCategoryResponse getAllPersonality() {
        return profileConverter.toPersonalityCategoryResponse();
    }

    public PersonalityUpdateResponse updatePersonality(Long memberId, PersonalityUpdateRequest request) {
        Member member = getMember(memberId);

        Preference preference = getPreference(member);
        if (preference == null) {
            preference = Preference.create(
                member,
                request.socialType(),
                request.meetingType(),
                request.chatType(),
                request.friendType(),
                request.relationType()
            );
            preferenceRepository.save(preference);
        } else {
            preference.update(
                request.socialType(),
                request.meetingType(),
                request.chatType(),
                request.friendType(),
                request.relationType()
            );
        }
        return profileConverter.toPersonalityUpdateResponse(preference);
    }

    @Transactional(readOnly = true)
    public MyProfileResponse getMyProfile(Long memberId) {
        Member member = getMember(memberId);

        Preference preference = getPreference(member);

        BadgeResponse badge = badgeService.getBadgeSummary(member.getId());

        List<InterestMember> interestMembers = interestMemberRepository.findAllByMember(member);
        List<Interest> interests = interestMembers.stream()
            .map(InterestMember::getInterest)
            .toList();

        return profileConverter.toProfileResponse(member, interests, preference, badge);
    }

    // 다른 사람 프로필 조회
    @Transactional(readOnly = true)
    public OtherProfileResponse getOtherProfile(Long memberId, Long targetMemberId) {
        Member me = getMember(memberId);
        Member target = getMember(targetMemberId);

        MemberLocation myLocation = memberLocationRepository.findByMember(me).orElse(null);
        MemberLocation targetLocation = memberLocationRepository.findByMember(target).orElse(null);
        // 나와의 거리 계산
        String distance = calculateDistance(
            myLocation.getLatitude(), myLocation.getLongitude(),
            targetLocation.getLatitude(), targetLocation.getLongitude()
        );

        Preference preference = getPreference(target);
        BadgeResponse badge = badgeService.getBadgeSummary(target.getId());
        List<InterestMember> interestMembers = interestMemberRepository.findAllByMember(target);
        List<Interest> interests = interestMembers.stream()
            .map(InterestMember::getInterest)
            .toList();

        return profileConverter.toOtherProfileResponse(target, interests, preference, distance, badge);
    }


    public void toggleEvaluation(Long fromId, Long toId, EvaluationType type) {
        Member from = getMember(fromId);
        Member to = getMember(toId);

        Evaluation existing = evaluationRepository.findByFromMemberAndToMember(from, to)
            .orElse(null);

        // 없는 경우
        if (existing == null) {
            evaluationRepository.save(new Evaluation(from, to, type));

            if (type == EvaluationType.RECOMMEND) to.increaseRecommend();
            else to.increaseNotRecommend();

            // 있음 -> 깉은 버튼 클릭 -> 취소
        } else if (existing.getType() == type) {
            evaluationRepository.delete(existing);

            if (type == EvaluationType.RECOMMEND) to.decreaseRecommend();
            else to.decreaseNotRecommend();

            // 있음 -> 다른 버튼 클릭 switch ex) 추천 눌려있는데 비추천 누르는 경우
        } else {
            // 기존꺼 취소
            if (existing.getType() == EvaluationType.RECOMMEND) to.decreaseRecommend();
            else to.decreaseNotRecommend();

            // 새것 적용
            if (type == EvaluationType.RECOMMEND) to.increaseRecommend();
            else to.increaseNotRecommend();

            existing.updateType(type);
        }

        badgeService.checkPositiveEvaluation(toId);
    }

    // 사용자 찾기 공통 로직
    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
            () -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
    }

    // 나와의 거리 계산 메소드
    private String calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return "알 수 없음"; // 좌표 없는 경우 처리
        }

        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));

        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344; // Mile -> km 변환

        // 소수점 둘째 자리까지 포맷팅 (예: "2.50km")
        return String.format("%.2fkm", dist);

    }

    // 선호도 찾기 공통 로직
    @Nullable
    private Preference getPreference(Member member) {
        return preferenceRepository.findById(member.getId()).orElse(null);
    }
}
