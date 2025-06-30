package com.minjisuper.inquiry.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Profile("!dev") // dev 프로파일이 아닐 때만 활성화
public class NaverSmsService implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(NaverSmsService.class);

    @Value("${ncloud.accessKey}")
    private String accessKey;

    @Value("${ncloud.secretKey}")
    private String secretKey;

    @Value("${ncloud.serviceId}")
    private String serviceId;

    @Value("${ncloud.senderPhone}")
    private String senderPhone;

    @Value("${ncloud.recipientPhone:01098765432}")
    private String recipientPhone;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean sendSms(String recipientPhone, String message) {
        return sendSms(recipientPhone, message, "SMS");
    }

    /**
     * SMS 또는 LMS 메시지를 발송합니다.
     *
     * @param recipientPhone 수신자 전화번호
     * @param message 메시지 내용
     * @param messageType SMS 또는 LMS
     * @return 발송 성공 여부
     */
    public boolean sendSms(String recipientPhone, String message, String messageType) {
        if (!isServiceAvailable()) {
            logger.error("NCP SMS 서비스 설정이 올바르지 않습니다.");
            return false;
        }
        try {
            logger.info("SMS 발송 시작 - 수신자: {}, 메시지 길이: {}",
                       recipientPhone.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-****-$3"),
                       message.length());

            String timestamp = String.valueOf(System.currentTimeMillis());
            String requestUrl = "https://sens.apigw.ntruss.com/sms/v2/services/" + serviceId + "/messages";

            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("x-ncp-apigw-timestamp", timestamp);
            connection.setRequestProperty("x-ncp-iam-access-key", accessKey);
            connection.setRequestProperty("x-ncp-apigw-signature-v2", makeSignature(timestamp));
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000); // 5초 연결 타임아웃
            connection.setReadTimeout(10000);   // 10초 읽기 타임아웃

            // 메시지 길이에 따라 SMS/LMS 자동 결정
            String actualMessageType = message.length() > 90 ? "LMS" : "SMS";

            Map<String, Object> requestData = new HashMap<>();
            requestData.put("type", actualMessageType);
            requestData.put("contentType", "COMM");
            requestData.put("from", senderPhone);
            requestData.put("content", message);

            if ("LMS".equals(actualMessageType)) {
                requestData.put("subject", "[창업 문의 접수 알림]"); // LMS 제목
            }

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> messageObject = new HashMap<>();
            messageObject.put("to", recipientPhone);
            messages.add(messageObject);
            requestData.put("messages", messages);

            String jsonBody = objectMapper.writeValueAsString(requestData);
            logger.debug("NCP SMS 요청 데이터: {}", jsonBody);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();

            // 응답 본문 읽기
            String responseBody = readResponse(connection);

            if (responseCode == 202) { // NCP SMS API는 202 Accepted를 반환
                logger.info("SMS 발송 성공 - 응답 코드: {}, 메시지 타입: {}", responseCode, actualMessageType);
                logger.debug("SMS 발송 응답: {}", responseBody);
                return true;
            } else {
                logger.error("SMS 발송 실패 - 응답 코드: {}, 메시지: {}, 응답 본문: {}",
                           responseCode, responseMessage, responseBody);
                return false;
            }

        } catch (Exception e) {
            logger.error("SMS 발송 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * HTTP 응답 본문을 읽습니다.
     */
    private String readResponse(HttpURLConnection connection) {
        try {
            BufferedReader reader;
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } catch (Exception e) {
            logger.warn("응답 읽기 실패: {}", e.getMessage());
            return "";
        }
    }

    /**
     * NCP API 인증을 위한 서명을 생성합니다.
     */
    private String makeSignature(String timestamp) throws Exception {
        String method = "POST";
        String url = "/sms/v2/services/" + serviceId + "/messages";
        String message = method + " " + url + "\n" + timestamp + "\n" + accessKey;

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(rawHmac);
    }

    @Override
    public boolean isServiceAvailable() {
        boolean isConfigured = accessKey != null && !accessKey.equals("YOUR_ACCESS_KEY") &&
                              secretKey != null && !secretKey.equals("YOUR_SECRET_KEY") &&
                              serviceId != null && !serviceId.equals("YOUR_SERVICE_ID") &&
                              senderPhone != null && senderPhone.matches("^010\\d{8}$");

        if (!isConfigured) {
            logger.warn("NCP SMS 서비스 설정이 완료되지 않았습니다. application.properties를 확인하세요.");
        }

        return isConfigured;
    }
}
