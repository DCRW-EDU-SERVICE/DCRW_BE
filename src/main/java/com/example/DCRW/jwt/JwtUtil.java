package com.example.DCRW.jwt;


import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value; // properties에서 값 가져옴
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// jwt 생성 및 검증
@Component
public class JwtUtil {
    private SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("usesrname", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String getCategory(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    // payload에 저장될 정보 (username, role, 생성일, 만료일)
    public String createJwt(String category, String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("username", username) // payload에 claim으로 데이터 넣을 수 있음
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) // 언제 발행 됐는지(현재 발행 시간)
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 언제 소멸될 것인지
                .signWith(secretKey) // 시그니처를 만들어 암호화 진행
                .compact(); // 토큰 compact
    }
}
