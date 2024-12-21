package member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new User(member.getUsername(), member.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(member.getRole())));
    }

    public void saveAdminAccount(String username, String rawPassword) {
        String encryptedPassword = passwordEncoder.encode(rawPassword);
        Member admin = new Member();
        admin.setUsername(username);
        admin.setPassword(encryptedPassword);
        admin.setRole("ROLE_ADMIN");
        memberRepository.save(admin);
    }
}

