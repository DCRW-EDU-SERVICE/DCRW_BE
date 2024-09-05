package com.example.DCRW.session;

import com.example.DCRW.dto.user.CustomUserDetails;
import com.example.DCRW.dto.user.LoginDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.util.StreamUtils;

// 로그인 필터 - 세션 기반
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public LoginFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 요청을 가로채서 요청에 담겨있는 username, password 받기
        LoginDto loginDto;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            loginDto = objectMapper.readValue(messageBody, LoginDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String username = loginDto.getUserName();
        String password = loginDto.getPassword();

        // 스프링 시큐리티에서 username과 password를 검증하기 위해 token에 담아야 함
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // 사용자 IP 주소 및 세션 정보 설정
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        // 인증 성공 시 SecurityContextHolder에 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 세션 생성 및 사용자 정보 저장
        HttpSession session = request.getSession(true);  // 세션이 없으면 새로 생성

        // 사용자 정보를 세션에 저장하기 전에 SecurityContext를 세션에 저장해야 함
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        // 올바른 사용자 정보를 세션에 저장
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        session.setAttribute("username", userDetails.getUsername());  // 세션에 username 저장
        session.setAttribute("role", userDetails.getAuthorities().iterator().next().getAuthority());

        // 응답에 세션 ID를 쿠키로 설정
        response.addCookie(new Cookie("JSESSIONID", session.getId()));

        // 응답을 JSON 형식으로 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        // JSON 형식으로 응답
        String jsonResponse = "{\"message\": \"Login successful\"}";
        response.getWriter().write(jsonResponse);
    }



    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        // 인증 실패 시 상태 코드 설정

        // 응답을 JSON 형식으로 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        // JSON 형식으로 응답
        String jsonResponse = "{\"message\": \"Login failed\"}";
        response.getWriter().write(jsonResponse);
    }
}
