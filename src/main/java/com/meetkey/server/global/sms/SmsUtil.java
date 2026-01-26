package com.meetkey.server.global.sms;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class SmsUtil {
    // 6자리 난수 생성
    public static String createCode() {
        Random random = new Random();
        int code = 10000 + random.nextInt(90000);
        return String.valueOf(code);
    }

    //
    public String makeAuthMessage(String authCode) {
        return "[MeetKey 인증번호] " + authCode + "\n본인 확인을 위해 인증번호를 입력해주세요.";
    }
}
