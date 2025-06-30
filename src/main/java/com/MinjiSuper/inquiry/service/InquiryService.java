package com.minjisuper.inquiry.service;

import com.minjisuper.inquiry.entity.Inquiry;
import com.minjisuper.inquiry.repository.InquiryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InquiryService {

    private static final Logger logger = LoggerFactory.getLogger(InquiryService.class);

    private final InquiryRepository inquiryRepository;
    private final SmsService smsService; // 인터페이스 사용으로 변경

    @Value("${ncloud.recipientPhone:01098765432}")
    private String recipientPhone;

    public InquiryService(InquiryRepository inquiryRepository, SmsService smsService) {
        this.inquiryRepository = inquiryRepository;
        this.smsService = smsService;
    }

    @Transactional
    public Inquiry saveInquiry(Inquiry inquiry) {
        logger.info("문의 접수 처리 시작 - 이름: {}, 창업유형: {}", inquiry.getName(), inquiry.getBusinessType());

        Inquiry savedInquiry = inquiryRepository.save(inquiry);
        logger.info("문의 데이터 저장 완료 - ID: {}", savedInquiry.getId());

        // 약관 동의한 경우에만 문자 전송
        if (inquiry.getAgreed() != null && inquiry.getAgreed()) {
            sendNotificationSms(inquiry);
        } else {
            logger.info("약관 동의하지 않음 - SMS 발송 생략");
        }

        return savedInquiry;
    }

    /**
     * 문의 접수 알림 SMS를 발송합니다.
     */
    private void sendNotificationSms(Inquiry inquiry) {
        try {
            String smsMessage = buildSmsMessage(inquiry);

            if (!smsService.isServiceAvailable()) {
                logger.warn("SMS 서비스를 사용할 수 없습니다.");
                return;
            }

            boolean success = smsService.sendSms(recipientPhone, smsMessage);

            if (success) {
                logger.info("문의 접수 알림 SMS 발송 성공 - 문의 ID: {}", inquiry.getId());
            } else {
                logger.error("문의 접수 알림 SMS 발송 실패 - 문의 ID: {}", inquiry.getId());
            }

        } catch (Exception e) {
            logger.error("SMS 발송 중 예외 발생 - 문의 ID: {}, 오류: {}", inquiry.getId(), e.getMessage(), e);
        }
    }

    /**
     * SMS 메시지 내용을 구성합니다.
     */
    private String buildSmsMessage(Inquiry inquiry) {
        return "[창업 문의 접수 알림]\n" +
               "이름: " + inquiry.getName() + "\n" +
               "연락처: " + inquiry.getPhone() + "\n" +
               "이메일: " + inquiry.getEmail() + "\n" +
               "창업유형: " + inquiry.getBusinessType() + "\n" +
               "희망지역: " + inquiry.getProvince() + " " + inquiry.getCity() + "\n" +
               "접수시간: " + inquiry.getCreatedAt().toString().substring(0, 19);
    }
}
