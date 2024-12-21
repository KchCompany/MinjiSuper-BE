package member;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

    @GetMapping("/api/admin/check")
    public String checkAdminAccess() {
        return "관리자 인증 성공!";
    }
}
