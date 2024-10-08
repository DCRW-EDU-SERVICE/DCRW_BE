package com.example.DCRW.config;

import com.example.DCRW.service.user.CustomUserDetailsService;
import com.example.DCRW.session.LoginFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity // security 관련 configuration
public class SecurityConfig{
    private final AuthenticationConfiguration authenticationConfiguration;


    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, CustomUserDetailsService customUserDetailsService) {
        this.authenticationConfiguration = authenticationConfiguration;
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

                                return configuration;

                            }
                        }));

        // Form 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());


        // http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        // 인가 작업(경로에 따른 허용 권한 접근 관리)
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // CSRF 토큰을 쿠키로 전달
                        .ignoringRequestMatchers("/login", "/signup") // 로그인 엔드포인트에 대해 CSRF 보호 비활성화
                )
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "/signup", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // 모든 권한 허용
                        .requestMatchers("/admin/**").hasRole("ADMIN") // admin이라는 접근은 ADMIN 권한만 가능
                        .requestMatchers("/teacher/**").hasRole("TEACHER")// teacher 권한만 접근 가능
                        .anyRequest().authenticated()); // 이외 접근은 로그인 한 사용자만 접근 가능

        // 필터 등록(시큐리티가 동작할 때 필터가 동작할 수 있도록) - UsernamePasswordAuthenticationFilter 대체해서 필터를 등록하기 때문에 그 자리에 등록하기 위해 addFilterAt()
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration)), UsernamePasswordAuthenticationFilter.class);

        // 로그아웃 설정
        http.logout(logout -> logout
                .logoutUrl("/logout") // 로그아웃 URL 설정
                .logoutSuccessUrl("/") // 로그아웃 성공 후 리다이렉트할 URL 설정
                .invalidateHttpSession(true) // 세션 무효화 설정
                .deleteCookies("JSESSIONID") // 특정 쿠키 삭제 설정
                .addLogoutHandler(new SecurityContextLogoutHandler()) // 로그아웃 핸들러 추가
                .permitAll()); // 모든 사용자에게 로그아웃 허용

        // 세션 설정
        http
                .sessionManagement((session) -> session
                        // 로그인 시 세션을 새로 생성
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession) // 세션 고정 방지 설정
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션 필요할 때만 생성
                        .maximumSessions(1) // 하나의 아이디에 대한 다중 로그인 허용 개수 설정
                        .maxSessionsPreventsLogin(false)); // 다중 로그인 개수를 초과했을 경우 true 시 새로운 로그인 차단, false시 기존 세션 삭제

        return http.build();
    }
}