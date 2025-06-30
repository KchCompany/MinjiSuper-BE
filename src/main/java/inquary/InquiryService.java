package inquary;

import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    // private final NaverSmsService smsService;
    private final FakeNaverSmsService smsService; // 기존 NaverSmsService 대신 Fake 사용

    public InquiryService(InquiryRepository inquiryRepository, FakeNaverSmsService smsService) {
        this.inquiryRepository = inquiryRepository;
        this.smsService =  smsService;
    }

    @Transactional
    public Inquiry saveInquiry(Inquiry inquiry) {
        Inquiry savedInquiry = inquiryRepository.save(inquiry);

        // 약관 동의한 경우에만 문자 전송
        if (inquiry.isAgreed()) {
            String smsMessage = "[문의 접수 알림]\n"
                    + "이름: " + inquiry.getName() + "\n"
                    + "연락처: " + inquiry.getPhone() + "\n"
                    + "희망 지역: " + inquiry.getDesiredArea() + "\n"
                    + "문의 내용: " + inquiry.getMessage();

            smsService.sendSms("01098765432", smsMessage); // 담당자 전화번호
        }

        return savedInquiry;
    }
}
