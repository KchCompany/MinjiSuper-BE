package com.minjisuper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 프론트엔드 도메인 허용
        configuration.addAllowedOrigin("http://minji-super.s3-website.kr.object.ncloudstorage.com");
        configuration.addAllowedOrigin("https://minji-super.s3-website.kr.object.ncloudstorage.com");
        configuration.addAllowedOrigin("http://localhost:3000"); // 로컬 개발용
        configuration.addAllowedOrigin("http://localhost:8080"); // 로컬 개발용
        
        // 허용할 HTTP 메서드
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("OPTIONS");
        
        // 허용할 헤더
        configuration.addAllowedHeader("*");
        
        // 인증 정보 포함 허용
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
