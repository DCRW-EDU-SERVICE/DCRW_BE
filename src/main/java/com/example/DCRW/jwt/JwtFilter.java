package com.example.DCRW.jwt;

import com.example.DCRW.dto.CustomUserDetails;
import com.example.DCRW.entity.Users;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 세션 생성
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // request에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");

        if(authorization == null || !authorization.startsWith("Bearer ")){
//            filterChain.doFilter(request, response); // 다음 필터로 request, response 넘기기
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("no token");
            return;
        }

        // Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1];

        // 토큰 소멸 시간 검증
        if(jwtUtil.isExpired(token)){
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        // User Entity를 생성하여 값 set
        Users users = new Users();
        users.setUserId(username);
        users.setPassword("temppassword"); // DB에 요청이 올 때마다 매번 db를 조회하는 안좋은 상황이 발생하기 때문에 context에 진짜 비밀번호가 들어갈 필요 없다(?)
        users.setRoleCode(Integer.parseInt(role));

        CustomUserDetails customUserDetails = new CustomUserDetails(users);

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // 세션에 사용자 등록 - 특정 경로에 접근할 수 있음
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 그 다음 필터에 전달
        filterChain.doFilter(request, response);
    }
}
