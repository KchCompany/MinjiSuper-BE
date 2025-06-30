package com.minjisuper.inquiry.service;

import com.minjisuper.inquiry.entity.Inquiry;
import com.minjisuper.inquiry.repository.InquiryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    // private final NaverSmsService smsService;
    private final FakeNaverSmsService smsService; // 기존 NaverSmsService 대신 Fake 사용

    public InquiryService(InquiryRepository inquiryRepository, FakeNaverSmsService smsService) {
        this.inquiryRepository = inquiryRepository;
        this.smsService = smsService;
    }

    @Transactional
    public Inquiry saveInquiry(Inquiry inquiry) {
        Inquiry savedInquiry = inquiryRepository.save(inquiry);

        // 약관 동의한 경우에만 문자 전송
        if (inquiry.getAgreed() != null && inquiry.getAgreed()) {
            String smsMessage = "[창업 문의 접수 알림]\n"
                    + "이름: " + inquiry.getName() + "\n"
                    + "연락처: " + inquiry.getPhone() + "\n"
                    + "이메일: " + inquiry.getEmail() + "\n"
                    + "창업유형: " + inquiry.getBusinessType() + "\n"
                    + "희망지역: " + inquiry.getProvince() + " " + inquiry.getCity();

            smsService.sendSms("01098765432", smsMessage); // 담당자 전화번호
        }

        return savedInquiry;
    }
}
