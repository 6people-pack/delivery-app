package com.sparta.delivery.global.config;

import com.sparta.delivery.security.filter.JwtAuthorizationFilter;
import com.sparta.delivery.security.JwtUtil;
import com.sparta.delivery.security.userdetails.UserDetailsServiceImpl;
import com.sparta.delivery.user.repository.RefreshTokenRepository;
import com.sparta.delivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // 비밀번호 암호화를 위해
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // jwt 검증(요청 시 권한 검증)
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService, userRepository, refreshTokenRepository);
    }

    // security 정책 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF는 폼 기반 인증에서 발생할 수 있는 공격 기법으로, CSRF 토큰으로 보호
        // jwt는 구조적으로 CSRF공격이 불가능하기 때문에 비활성
        http.csrf((csrf) -> csrf.disable());

        // jwt(토큰 기반 인증 방식)는 세션을 필요로 하지 않음, STATELESS -> 완전 사용 안함
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                        .requestMatchers("/api/user/signup", "/api/**").permitAll() // 회원가입, 로그인 접근 허용
                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );


        // 필터 관리
        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}