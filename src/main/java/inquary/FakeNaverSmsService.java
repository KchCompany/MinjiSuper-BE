package inquary;

import org.springframework.stereotype.Service;

@Service
public class FakeNaverSmsService {
    public void sendSms(String recipientPhone, String message) {
        System.out.println("[Mock SMS] 전송 완료!");
        System.out.println("수신자: " + recipientPhone);
        System.out.println("메시지 내용: " + message);
    }
}
