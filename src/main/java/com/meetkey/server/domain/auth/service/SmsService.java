package com.meetkey.server.domain.auth.service;

import com.meetkey.server.domain.auth.exception.AuthErrorStatus;
import com.meetkey.server.domain.auth.exception.AuthException;
import com.meetkey.server.global.sms.SmsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class SmsService {

    private final DefaultMessageService messageService;
    private final SmsUtil smsUtil;
    private final StringRedisTemplate redisTemplate;

    @Value("${coolsms.sender}")
    private String sender;

    /*
     * 인증번호 발송 로직
     */
    public void sendAuthCode(String phoneNumber) {
        String authCode = smsUtil.createCode(); // 인증번호 생성
        String messageText = smsUtil.makeAuthMessage(authCode); // 메세지 포멧팅

        Message message = new Message();
        message.setFrom(sender);
        message.setTo(phoneNumber); // 요청한 사람의 번호
        message.setText(messageText);

        SingleMessageSendingRequest request = new SingleMessageSendingRequest(message);

        try {
            SingleMessageSentResponse response = messageService.sendOne(request);
            log.info("[SMS] 인증번호 발송 - to: {}, code: {}, response: {}", phoneNumber, authCode, response);
        } catch (Exception e) {
            throw new AuthException(AuthErrorStatus.SEND_FAILED);

        }

        // redis에 인증번호 저장
        String redisKey = "SMS:AUTH:" + phoneNumber;
        redisTemplate.opsForValue().set(redisKey, authCode, 3, TimeUnit.MINUTES); // 3분 유효
        log.info("[SMS] 인증번호 Redis 저장 - key: {}, code: {}", redisKey, authCode);

    }

    /*
     * 인증번호 검증 로직
     */
    public boolean verifyAuthCode(String phone, String inputCode) {

        String redisKey = "SMS:AUTH:" + phone;
        String storedCode = redisTemplate.opsForValue().get(redisKey); // 인증기한 180초
        log.info("[SMS] 인증번호 검증 요청 - key: {}, 입력값: {}, 저장값: {}", redisKey, inputCode, storedCode);

        if (storedCode != null && storedCode.equals(inputCode)) {
            redisTemplate.delete(redisKey);
            log.info("[SMS] 인증번호 검증 성공 - key: {}", redisKey);
            return true;
        } else {
            throw new AuthException(AuthErrorStatus.VERIFY_FAILED);
        }
    }
}
