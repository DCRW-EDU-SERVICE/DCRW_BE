package com.example.DCRW.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // security 관련 configuration
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // csrf disable - jwt는 세션을 stateless 상태로 관리하기 때문에 csrf 공격을 방어하지 않아도 되서 기본적으로 disable
        http
                .csrf((auth) -> auth.disable());

        // Form 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        // http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        // 인가 작업(경로에 따른 허용 권한 접근 관리)
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "join").permitAll() // 모든 권한 허용
                        .requestMatchers("/admin").hasRole("ADMIN") // admin이라는 접근은 ADMIN 권한만 가능
                        .anyRequest().authenticated()); // 이외 접근은 로그인 한 사용자만 접근 가능

        // 세션 설정 - jwt는 항상 stateless 상태로 관리해야 한다. 매우 중요
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }
}