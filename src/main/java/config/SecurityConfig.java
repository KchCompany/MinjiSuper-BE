package config;

import member.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig { // Spring Security를 사용하여 애플리케이션의 인증 및 권한 관리 설정하는 클래스

    private final MemberService memberService;

    public SecurityConfig(MemberService memberService) {
        this.memberService = memberService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 암호화 사용
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // 주요 설정
        http
                // CSRF 설정
                .csrf(csrf -> csrf.disable()) // 비활성화 (테스트용), Rest Api 사용하는 경우 주로 비활성화함

                // 인증 및 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // 관리자 전용 경로
                        .anyRequest().permitAll() // 그 외 요청은 허용 - 인증 없이 누구나 접근 가능
                )

                // 로그인 설정
                .formLogin(form -> form
                        .loginPage("/login") // 사용자 정의 로그인 페이지 경로
                        .defaultSuccessUrl("/api/admin/check", true) // 로그인 성공 시 이동 경로
                        .permitAll() // 로그인 페이지는 누구나 접근 가능
                )

                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(memberService) // 사용자 인증 정보를 DB 기반 인증
                .passwordEncoder(passwordEncoder()); // 사용자가 입력한 비밀번호 암호화하여 DB 값과 비교

        // .and() 제거 후 바로 build()
        return authenticationManagerBuilder.build();
    }
}
