package com.sparta.delivery.security;

import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.global.unit.utils.CookieUtils;
import com.sparta.delivery.user.domain.RefreshToken;
import com.sparta.delivery.user.domain.User;
import com.sparta.delivery.user.dto.RefreshTokenDto;
import com.sparta.delivery.user.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
@RequiredArgsConstructor
public class JwtUtil {
    // Header accessToken KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // accessToken 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // 엑세스 토큰 만료시간 application.yml참조로 바꾸기
    @Value("${jwt.expiration.accesstoken}")
    private long ACCESS_TOKEN_TIME;
    // 리프레시 토큰 만료시간
    @Value("${jwt.expiration.refreshtoken}")
    private long REFRESH_TOKEN_TIME;

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private final RefreshTokenRepository refreshTokenRepository;

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
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token); //파싱하는 과정에서 유효성 검증에 실패하면 자동으로 예외가 발생함
        } catch (JwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명입니다.");
            throw new BusinessException(ErrorCode.INVALID_JWT_TOKEN); // 401
        }
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    //엑세스 토큰 발급 및 헤더 등록
    public void issueAndSetAccessToken(HttpServletResponse response, String email) {

        String accessToken = issueAccessToken(email);           // accessToken 발급
        response.setHeader("Authorization", accessToken); // accessToken은 헤더에 저장
    }

    //리프레시 토큰 발급 및 쿠키, db저장
    public void issueAndSetRefreshToken(HttpServletResponse response, User user) {

        refreshTokenRepository.deleteByUser(user); // db에 리프레시 토큰 있으면 삭제

        // 만료 시간도 받아오기 위해 Dto로 전달
        RefreshTokenDto refreshTokenDto = issueRefreshToken(user.getEmail());
        String refreshToken = refreshTokenDto.token();
        Date exp = refreshTokenDto.exp();

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .refreshToken(refreshToken)
                        .user(user)
                        .exp(exp)
                        .build()
        );

        Duration ttlTime = Duration.between(
                Instant.now(),
                exp.toInstant()
        );

        // refreshToken은 http only 쿠키 방식으로 클라이언트에게 줌, ttlTime만큼 시간이 경과하면 삭제
        CookieUtils.setRefreshTokenCookie(response, refreshToken, ttlTime);
    }

    // 리프레시 토큰 검증
    public void validateRefreshToken(User user, String refreshToken) {
        RefreshToken dbToken = refreshTokenRepository.findRefreshTokenByUser(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_JWT_TOKEN));

        if (!dbToken.getRefreshToken().equals(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_JWT_TOKEN);
        }
    }

}