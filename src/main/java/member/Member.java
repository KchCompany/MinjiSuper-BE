package member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "member") // DB의 member 테이블과 매핑
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // 관리자 ID (예: kchcompany)

    @Column(nullable = false)
    private String password; // bcrypt로 암호화된 비밀번호

    @Column(nullable = false)
    private String role; // 권한 (예: ADMIN)
}
