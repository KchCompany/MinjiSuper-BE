package com.minjisuper.inquiry.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev") // dev 프로파일일 때만 활성화
public class FakeNaverSmsService implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(FakeNaverSmsService.class);

    @Override
    public boolean sendSms(String recipientPhone, String message) {
        logger.info("=== [MOCK SMS] 전송 시뮬레이션 ===");
        logger.info("수신자: {}", recipientPhone.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-****-$3"));
        logger.info("메시지 길이: {} 자", message.length());
        logger.info("메시지 내용:");
        logger.info("{}", message);
        logger.info("=== [MOCK SMS] 전송 완료 ===");

        // 개발 환경에서는 항상 성공으로 처리
        return true;
    }

    @Override
    public boolean isServiceAvailable() {
        return true; // Mock 서비스는 항상 사용 가능
    }
}
