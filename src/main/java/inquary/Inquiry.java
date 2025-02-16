package inquary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    private String name;

    @NotBlank(message = "전화번호를 입력하세요.")
    @Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호를 입력하세요 (010-xxxx-xxxx)")
    private String phone;

    @NotBlank(message = "문의 내용을 입력하세요.")
    @Column(columnDefinition = "TEXT")
    private String message;

    private boolean agreed;  // 약관 동의 여부

    private LocalDateTime createdAt = LocalDateTime.now();
}

