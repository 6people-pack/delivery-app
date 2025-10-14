package com.sparta.delivery.security.filter;

import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.global.unit.utils.CookieUtils;
import com.sparta.delivery.security.JwtUtil;
import com.sparta.delivery.security.userdetails.UserDetailsServiceImpl;
import com.sparta.delivery.user.domain.User;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest req,
            @NonNull HttpServletResponse res,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 헤더에서 엑세스토큰 가져옴
        String accessToken = jwtUtil.getJwtFromHeader(req);
        if (StringUtils.hasText(accessToken)) { // 공백이 아닌 문자열이 있으면 true(= 토큰이 있다면)

            try {
                Claims info = jwtUtil.getUserInfoFromToken(accessToken); // 파싱 및 검증
                setAuthentication(info.getSubject());                    // 인증 진행, getSubject() = email

            } catch (ExpiredJwtException e) { //엑세스 토큰 만료
                String email = e.getClaims().getSubject();
                String refreshToken = CookieUtils.getRefreshTokenCookie(req); // 쿠키에서 리프레시 토큰 가져옴
                jwtUtil.validateToken(refreshToken);                    // 갖고있는 리프레시 jwt토큰 자체를 검증

                // db에 있는 토큰과 동일한 토큰인지 검증
                User user = userDetailsService.findByEmailOrElseThrow(email);
                jwtUtil.validateRefreshToken(user, refreshToken);

                // 새로운 엑세스 토큰 발급
                jwtUtil.issueAndSetAccessToken(res, email);
                setAuthentication(email);

            } catch (JwtException e) {
                log.error("Invalid JWT signature, 유효하지 않는 JWT 서명입니다.");
                throw new BusinessException(ErrorCode.INVALID_JWT_TOKEN); // 401
            }

        }
        if (!StringUtils.hasText(accessToken)) { log.error("토큰이 없는 요청");}

        // 토큰이 없으면 바로 다음 필터(=인증 절차 건너뛰기)
        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();   //인증 정보를 담을 컨테이너
        Authentication authentication = createAuthentication(username);         //인증 객체 생성
        context.setAuthentication(authentication);                              //컨테이너에 객체 저장

        //Spring Security가 전역적으로 SecurityContext를 참조하게 해주는 Holder에 컨테이너 설정
        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Principal(사용자정보), Credentials(비빌번호), Authorities(권한) 순서
        // 비밀번호는 현재 인증된 상태라 null, 권한도 db에서 직접 확인할 예정이라 null
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

}