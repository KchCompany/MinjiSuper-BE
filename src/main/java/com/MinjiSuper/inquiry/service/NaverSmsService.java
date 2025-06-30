package com.minjisuper.inquiry.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class NaverSmsService {

    @Value("${ncloud.accessKey}")
    private String accessKey;

    @Value("${ncloud.secretKey}")
    private String secretKey;

    @Value("${ncloud.serviceId}")
    private String serviceId;

    @Value("${ncloud.senderPhone}")
    private String senderPhone;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendSms(String recipientPhone, String message) {
        try {
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

            Map<String, Object> requestData = new HashMap<>();
            requestData.put("type", "SMS");
            requestData.put("contentType", "COMM");
            requestData.put("from", senderPhone);
            requestData.put("content", message);

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> messageObject = new HashMap<>();
            messageObject.put("to", recipientPhone);
            messages.add(messageObject);
            requestData.put("messages", messages);

            String jsonBody = objectMapper.writeValueAsString(requestData);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            System.out.println("SMS 전송 응답 코드: " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String makeSignature(String timestamp) throws Exception {
        String method = "POST";
        String url = "/sms/v2/services/" + serviceId + "/messages";
        String message = method + " " + url + "\n" + timestamp + "\n" + accessKey;

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(rawHmac);
    }
}
