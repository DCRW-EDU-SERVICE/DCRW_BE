package com.example.DCRW.config;

import com.example.DCRW.jwt.JwtFilter;
import com.example.DCRW.jwt.JwtUtil;
import com.example.DCRW.jwt.LoginFilter;
import com.example.DCRW.repository.RefreshRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity // security 관련 configuration
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JwtUtil jwtUtil, RefreshRepository refreshRepository) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // cors 설정(로그인과 같은 필터 사용하는 기능에서도 방지)
        http
                .cors((cors) -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration configuration = new CorsConfiguration();

                                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); // 리액트 서버 포트 열어줌
                                configuration.setAllowedMethods(Collections.singletonList("*")); // get, post 등 모든 메소드 허용
                                configuration.setAllowCredentials(true); // credential 설정 true
                                configuration.setAllowedHeaders(Collections.singletonList("*"));
                                configuration.setMaxAge(3600L); // 허용 최대 시간
                                configuration.setExposedHeaders(Collections.singletonList("Authorization")); // Authorization 헤더에 보낼 것이기 때문에 허용

                                return null;
                            }
                        }));

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
                        .requestMatchers("/login", "/", "/signup", "/reissue").permitAll() // 모든 권한 허용
                        .requestMatchers("/admin").hasRole("0") // admin이라는 접근은 ADMIN 권한만 가능
                        .anyRequest().authenticated()); // 이외 접근은 로그인 한 사용자만 접근 가능

        // 필터 등록 - Login Filter 뒤에 토큰 검증 및 세션 생성하는 jwtFilter
        http
                .addFilterAfter(new JwtFilter(jwtUtil), LoginFilter.class);

        // 필터 등록(시큐리티가 동작할 때 필터가 동작할 수 있도록) - UsernamePasswordAuthenticationFilter 대체해서 필터를 등록하기 때문에 그 자리에 등록하기 위해 addFilterAt()
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository), UsernamePasswordAuthenticationFilter.class);

        // 세션 설정 - jwt는 항상 stateless 상태로 관리해야 한다. 매우 중요
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }
}