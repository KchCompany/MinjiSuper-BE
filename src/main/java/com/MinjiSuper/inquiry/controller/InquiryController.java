package com.minjisuper.inquiry.controller;

import com.minjisuper.inquiry.entity.Inquiry;
import com.minjisuper.inquiry.service.InquiryService;
import com.minjisuper.inquiry.service.SmsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/inquiry")
public class InquiryController {

    private final InquiryService inquiryService;
    private final SmsService smsService;

    public InquiryController(InquiryService inquiryService, SmsService smsService) {
        this.inquiryService = inquiryService;
        this.smsService = smsService;
    }

    @PostMapping
    public ResponseEntity<?> createInquiry(@Valid @RequestBody Inquiry inquiry, BindingResult bindingResult) {
        // 유효성 검사 실패 시 에러 응답
        if (bindingResult.hasErrors()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "입력 데이터가 올바르지 않습니다.");

            Map<String, String> fieldErrors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
            );
            errorResponse.put("errors", fieldErrors);

            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            Inquiry savedInquiry = inquiryService.saveInquiry(inquiry);

            // 성공 응답 (개인정보 보호를 위해 최소한의 정보만 반환)
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("message", "문의가 성공적으로 접수되었습니다.");
            successResponse.put("inquiryId", savedInquiry.getId());
            successResponse.put("createdAt", savedInquiry.getCreatedAt());

            return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);

        } catch (Exception e) {
            // 서버 에러 응답
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "문의 접수 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "inquiry-service");
        response.put("smsService", smsService.isServiceAvailable() ? "AVAILABLE" : "UNAVAILABLE");
        response.put("timestamp", java.time.LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sms-status")
    public ResponseEntity<Map<String, Object>> smsStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("available", smsService.isServiceAvailable());
        response.put("serviceType", smsService.getClass().getSimpleName());
        response.put("timestamp", java.time.LocalDateTime.now());

        if (!smsService.isServiceAvailable()) {
            response.put("message", "NCP SMS 서비스 설정을 확인하세요. NCP_SMS_SETUP_GUIDE.md 파일을 참조하세요.");
        }

        return ResponseEntity.ok(response);
    }
}
