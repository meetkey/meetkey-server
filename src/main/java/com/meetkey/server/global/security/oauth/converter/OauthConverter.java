package com.meetkey.server.global.security.oauth.converter;

import com.meetkey.server.domain.member.dto.MemberReqDTO;
import com.meetkey.server.global.security.oauth.dto.OauthReqDTO;

public class OauthConverter {
    public static MemberReqDTO.Signup toMemberSignUpDTO(OauthReqDTO.SignupReq req){
        return MemberReqDTO.Signup.builder()
                .birthday(req.birthday())
                .gender(req.gender())
                .firstLanguage(req.firstLanguage())
                .homeTown(req.homeTown())
                .phoneNumber(req.phoneNumber())
                .targetLanguage(req.targetLanguage())
                .targetLanguageLevel(req.targetLanguageLevel())
                .interests(req.interests())
                .meetingType(req.meetingType())
                .relationType(req.relationType())
                .chatType(req.chatType())
                .friendType(req.friendType())
                .socialType(req.socialType())
                .build();
    }
}
