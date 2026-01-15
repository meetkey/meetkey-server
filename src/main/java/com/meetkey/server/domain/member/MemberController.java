package com.meetkey.server.domain.member;

import com.meetkey.server.domain.member.exception.MemberErrorCode;
import com.meetkey.server.domain.member.exception.MemberException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {
    @GetMapping("/test")
    public void test() {
        throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
    }
}
