package com.example.DCRW.jwt;

import com.example.DCRW.dto.CustomUserDetails;
import com.example.DCRW.dto.LoginDto;
import com.example.DCRW.entity.RefreshEntity;
import com.example.DCRW.repository.RefreshRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

// 로그인
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, RefreshRepository refreshRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 요청을 가로채서 요청에 담겨있는 username, password 받기
        LoginDto loginDto = new LoginDto();

        try{
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            loginDto = objectMapper.readValue(messageBody, LoginDto.class);
        } catch (IOException e){
            throw new RuntimeException(e);
        }


        String username = loginDto.getUserName();
        String password = loginDto.getPassword();

        System.out.println(username);
        System.out.println(password);

        // 스프링 시큐리티에서 username과 password를 검증하기 위해 token에 담아야 함
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password, null); // 세번째 파라미터는 role

        // token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authenticationToken);
    }

    // 로그인 성공 시(여기서 jwt 발급)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication){
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        // 토큰 생성
        String access = jwtUtil.createJwt("access", username, role, 60*60*10L); // 프론트 로컬 스토리지에 저장
        String refresh = jwtUtil.createJwt("refresh", username, role, 8640000L); // 쿠키에 저장

        // Refresh 토큰 저장
        addRefreshEntity(username, refresh, 86400000L);

        // 응답
        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60); // 쿠키 생명주기
        //cookie.setSecure(true); //https 통신을 사용할 경우 넣기
        //cookie.setPath("/"); // 쿠키가 적용될 범위
        cookie.setHttpOnly(true); // 클라이언트 단에서 자바스크립트에서 해당 쿠키를 접근할 수 없도록

        return cookie;
    }

    // 로그인 실패 시
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed){
        response.setStatus(401);
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs){
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }
}
