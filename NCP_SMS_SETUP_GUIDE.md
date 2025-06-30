# 네이버 클라우드 플랫폼(NCP) SMS 서비스 설정 가이드

## 1. NCP 계정 생성 및 로그인
1. [네이버 클라우드 플랫폼](https://www.ncloud.com/) 접속
2. 회원가입 또는 로그인
3. 본인인증 및 결제수단 등록 완료

## 2. Simple & Easy Notification Service (SENS) 설정

### 2.1 SENS 서비스 신청
1. NCP 콘솔 → `Services` → `Application Service` → `Simple & Easy Notification Service` 선택
2. `이용 신청하기` 클릭
3. 서비스 이용약관 동의 후 신청

### 2.2 SMS 서비스 생성
1. SENS 콘솔 → `SMS` → `서비스 관리` 이동
2. `서비스 생성` 클릭
3. 서비스 정보 입력:
   - 서비스명: `MinjiSuper-SMS` (원하는 이름)
   - 설명: `창업 문의 SMS 알림 서비스`
4. `생성` 클릭

### 2.3 발신번호 등록
1. SMS 서비스 → `발신번호 관리` 이동
2. `발신번호 등록` 클릭
3. 발신번호 입력 (본인 명의 휴대폰 번호)
4. 인증 절차 완료 (SMS 인증)

## 3. API 인증 정보 발급

### 3.1 Sub Account 생성 (권장)
1. NCP 콘솔 → `마이페이지` → `계정 관리` → `Sub Account` 이동
2. `Sub Account 생성` 클릭
3. 계정 정보 입력:
   - Sub Account ID: `minjisuper-sms`
   - 설명: `SMS 서비스 전용 계정`
4. 권한 설정: `Simple & Easy Notification Service` 권한만 부여

### 3.2 Access Key 발급
1. Sub Account 선택 → `인증키 관리` 이동
2. `신규 API 인증키 생성` 클릭
3. Access Key ID와 Secret Key 확인 및 저장

## 4. 애플리케이션 설정

### 4.1 개발환경 설정 (application.properties)
```properties
# 개발환경에서는 FakeNaverSmsService 사용
spring.profiles.active=dev

# NCP SMS API 설정 (개발환경에서는 실제 발송되지 않음)
ncloud.accessKey=YOUR_ACCESS_KEY
ncloud.secretKey=YOUR_SECRET_KEY
ncloud.serviceId=YOUR_SERVICE_ID
ncloud.senderPhone=01012345678
ncloud.recipientPhone=01098765432
```

### 4.2 운영환경 설정
```bash
# 환경변수로 설정 (권장)
export NCP_ACCESS_KEY="실제_Access_Key"
export NCP_SECRET_KEY="실제_Secret_Key"
export NCP_SERVICE_ID="실제_Service_ID"
export NCP_SENDER_PHONE="01012345678"
export NCP_RECIPIENT_PHONE="01098765432"

# 운영환경으로 실행
java -jar -Dspring.profiles.active=prod MinjiSuper-BE.jar
```

## 5. 필요한 정보 정리

### 5.1 NCP 콘솔에서 확인해야 할 정보
- **Access Key**: Sub Account의 Access Key ID
- **Secret Key**: Sub Account의 Secret Key
- **Service ID**: SENS SMS 서비스의 서비스 ID
- **Sender Phone**: 등록된 발신번호 (하이픈 없이)
- **Recipient Phone**: 알림을 받을 담당자 번호

### 5.2 application.properties 설정 예시
```properties
# 실제 운영환경 설정
ncloud.accessKey=NKJSDF8234JKLSDF
ncloud.secretKey=SDKJF234LKJSDF234LKJSDF234LKJSDF234
ncloud.serviceId=ncp:sms:kr:123456789012:minjisuper-sms
ncloud.senderPhone=01012345678
ncloud.recipientPhone=01098765432
```

## 6. 테스트 방법

### 6.1 개발환경 테스트
```bash
# 개발환경에서는 콘솔에 로그만 출력됨
curl -X POST http://localhost:8080/api/inquiry \
  -H "Content-Type: application/json" \
  -d '{
    "name": "테스트",
    "phone": "01012345678",
    "email": "test@example.com",
    "businessType": "창업",
    "province": "서울특별시",
    "city": "강남구",
    "agreed": true
  }'
```

### 6.2 운영환경 테스트
1. 운영환경으로 애플리케이션 실행
2. 위와 동일한 API 호출
3. 실제 SMS 발송 확인

## 7. 주의사항

### 7.1 비용 관련
- SMS 발송 시 과금됨 (건당 약 15원)
- LMS 발송 시 더 높은 과금 (건당 약 45원)
- 테스트 시 소량으로 진행 권장

### 7.2 보안 관련
- API 키는 절대 코드에 하드코딩하지 말 것
- 환경변수 또는 보안 저장소 사용 권장
- Sub Account 사용으로 권한 최소화

### 7.3 발송 제한
- 동일 번호로 1일 최대 1,000건 발송 제한
- 스팸 방지를 위한 발송 패턴 모니터링

## 8. 문제 해결

### 8.1 자주 발생하는 오류
- **401 Unauthorized**: API 키 또는 서명 오류
- **403 Forbidden**: 권한 부족 또는 서비스 미신청
- **404 Not Found**: 잘못된 Service ID
- **400 Bad Request**: 발신번호 미등록 또는 잘못된 형식

### 8.2 로그 확인
```bash
# 애플리케이션 로그에서 SMS 발송 상태 확인
tail -f logs/application.log | grep SMS
```

## 9. 참고 링크
- [NCP SENS 가이드](https://guide.ncloud-docs.com/docs/sens-overview)
- [SMS API 레퍼런스](https://api.ncloud-docs.com/docs/ai-application-service-sens-smsv2)
- [NCP 콘솔](https://console.ncloud.com/)
