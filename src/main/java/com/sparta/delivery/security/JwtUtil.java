package com.sparta.delivery.security;

import com.sparta.delivery.user.dto.RefreshTokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {
    // Header accessToken KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // accessToken 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // 엑세스 토큰 만료시간 application.yml참조로 바꾸기
    private final long ACCESS_TOKEN_TIME = 1 * 60 * 1000L; // 1분
    // 리프레시 토큰 만료시간
    private final long REFRESH_TOKEN_TIME = 2 * 60 * 1000L; // 2분

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 엑세스 토큰 생성
    public String issueAccessToken(String email) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(email)                      //setSubject : 사용자를 식별할 고유한 값, claim : 추가로 담을 사용자 정보 Key:value 형식으로 저장
                        .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date)                      // 발급일
                        .signWith(key, signatureAlgorithm)      // 암호화 알고리즘
                        .compact();
    }

    // 리프레시 토큰 생성
    public RefreshTokenDto issueRefreshToken(String email) {
        Date date = new Date();
        Date exp = new Date(date.getTime() + REFRESH_TOKEN_TIME);

        String refreshToken = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(date)
                .setExpiration(exp)
                .signWith(key, signatureAlgorithm)
                .compact();

        return new RefreshTokenDto(refreshToken, exp);
    }

    // header 에서 JWT 엑세스 토큰 가져오기
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰 검증
    public void validateToken(String token) {
        Jwts.parserBuilder() //파싱하는 과정에서 유효성 검증에 실패하면 자동으로 예외가 발생함
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);


    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

}