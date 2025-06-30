package com.minjisuper.inquiry.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "이름을 입력하세요.")
    @Column(length = 50)
    private String name;

    @NotBlank(message = "연락처를 입력하세요.")
    @Pattern(regexp = "^010\\d{8}$", message = "올바른 연락처를 입력하세요 (01012345678 형식)")
    @Column(length = 11)
    private String phone;

    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "올바른 이메일 형식을 입력하세요.")
    @Column(length = 100)
    private String email;

    @NotBlank(message = "창업유형을 선택하세요.")
    @Pattern(regexp = "^(창업|업종변경)$", message = "창업 또는 업종변경 중 하나를 선택하세요.")
    @Column(length = 10)
    private String businessType;

    @NotBlank(message = "창업희망 시/도를 선택하세요.")
    @Column(length = 20)
    private String province;

    @NotBlank(message = "창업희망 시/군/구를 선택하세요.")
    @Column(length = 30)
    private String city;

    @NotNull(message = "약관 동의가 필요합니다.")
    private Boolean agreed;  // 약관 동의 여부

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
