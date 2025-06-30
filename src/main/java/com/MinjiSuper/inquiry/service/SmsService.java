package com.minjisuper.inquiry.service;

/**
 * SMS 발송 서비스 인터페이스
 */
public interface SmsService {
    
    /**
     * SMS 메시지를 발송합니다.
     * 
     * @param recipientPhone 수신자 전화번호 (01012345678 형식)
     * @param message 발송할 메시지 내용
     * @return 발송 성공 여부
     */
    boolean sendSms(String recipientPhone, String message);
    
    /**
     * SMS 서비스의 상태를 확인합니다.
     * 
     * @return 서비스 사용 가능 여부
     */
    boolean isServiceAvailable();
}
