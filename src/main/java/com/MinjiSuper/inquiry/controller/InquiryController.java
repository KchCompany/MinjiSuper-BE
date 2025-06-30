package com.minjisuper.inquiry.controller;

import com.minjisuper.inquiry.entity.Inquiry;
import com.minjisuper.inquiry.service.InquiryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inquiry")
public class InquiryController {

    private final InquiryService inquiryService;

    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @PostMapping
    public ResponseEntity<Inquiry> createInquiry(@Valid @RequestBody Inquiry inquiry) {
        Inquiry savedInquiry = inquiryService.saveInquiry(inquiry);
        return ResponseEntity.ok(savedInquiry);
    }
}
