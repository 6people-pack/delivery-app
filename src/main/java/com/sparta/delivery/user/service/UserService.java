package com.sparta.delivery.user.service;

import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.global.unit.utils.CookieUtils;
import com.sparta.delivery.security.jwt.dto.RefreshTokenResponseDto;
import com.sparta.delivery.security.jwt.utils.JwtUtil;
import com.sparta.delivery.security.service.TokenBlacklistService;
import com.sparta.delivery.user.domain.User;
import com.sparta.delivery.user.dto.*;
import com.sparta.delivery.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

import static io.jsonwebtoken.lang.Strings.hasText;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final TokenBlacklistService tokenBlacklistService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원 가입
    @Transactional
    public void signup(SignUpRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.email()).isPresent()) { // 이메일 중복
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);  //409
        }

        userRepository.save(User.createCustomer(
                requestDto.email(),
                passwordEncoder.encode(requestDto.password()),
                requestDto.name(),
                requestDto.nickname(),
                requestDto.phoneNumber(),
                requestDto.role()
        ));
    }

    //로그인
    @Transactional
    public void login(LoginRequestDto dto, HttpServletResponse response) {

        //가입된 email과 password가 같은지 확인
        User findUser = userRepository.findByEmail(dto.email()).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 입력된 비밀번호, 저장된 비밀번호 비교
        if (!passwordEncoder.matches(dto.password(), findUser.getPassword())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);  //404
        }

        // Token 발급
        String accessToken = jwtUtil.issueAccessToken(findUser.getEmail());
        RefreshTokenResponseDto refreshTokenResponseDto = jwtUtil.issueRefreshToken(findUser.getEmail());
        String refreshToken = refreshTokenResponseDto.token();

        // 리프레시 토큰 db 저장
        findUser.updateRefreshToken(refreshToken);

        // Token 헤더에 저장
        response.setHeader("Authorization", accessToken);

        // 원래는 리프레시 토큰도 헤더에 전송해서 프론트에서 쿠키로 등록하지만 프로트가 없어서 백엔드에서 쿠키등록
        // 리프레시 토큰을 쿠키에 넣기 위해 만료시간 계산
        Duration ttlTime = Duration.between(
                Instant.now(),
                refreshTokenResponseDto.exp().toInstant()
        );
        // refreshToken은 http only 쿠키 방식으로 저장, ttlTime만큼 시간이 경과하면 삭제
        CookieUtils.setRefreshTokenCookie(response, refreshToken, ttlTime);

    }

    // 엑세스 + 리프레시 토큰 재발급 및 등록
    @Transactional
    public void refreshJwtToken(User user, HttpServletRequest request, HttpServletResponse response) {
        User findUser = userRepository.findById(user.getId()).orElseThrow(
                ()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String cookieRefreshToken = CookieUtils.getRefreshTokenCookie(request); // 쿠키에서 리프레시 토큰 받아서
        jwtUtil.validateToken(cookieRefreshToken);                              // 토큰이 유효한지 검증

        if (!findUser.getRefreshToken().equals(cookieRefreshToken)) {           // db에 있는 토큰과 비교
            throw new BusinessException(ErrorCode.INVALID_JWT_TOKEN);
        }

        // Token 발급
        String newAccessToken = jwtUtil.issueAccessToken(findUser.getEmail());
        RefreshTokenResponseDto refreshTokenResponseDto = jwtUtil.issueRefreshToken(findUser.getEmail());
        String newRefreshToken = refreshTokenResponseDto.token();

        // 리프레시 토큰 db 저장
        findUser.updateRefreshToken(newRefreshToken);

        // Token 헤더에 저장
        response.setHeader("Authorization", newAccessToken);

        // 원래는 리프레시 토큰도 헤더에 전송해서 프론트에서 쿠키로 등록하지만 프로트가 없어서 백엔드에서 쿠키등록
        // 리프레시 토큰을 쿠키에 넣기 위해 만료시간 계산
        Duration ttlTime = Duration.between(
                Instant.now(),
                refreshTokenResponseDto.exp().toInstant()
        );

        // refreshToken은 http only 쿠키 방식으로 저장, ttlTime만큼 시간이 경과하면 삭제, 이 방식은 프론트에서도 리프레시토큰 접근 불가
        CookieUtils.setRefreshTokenCookie(response, newRefreshToken, ttlTime);

    }

    // 로그아웃
    // 자동 로그인 기능이 있다는 가정, 쿠키에 있는 리프레시 토큰과 db의 리프레시 토큰은 삭제 없이
    // 엑세스 토큰만 블랙리스트 처리
    @Transactional
    public void logout(HttpServletRequest request) {

        String accessToken = jwtUtil.getJwtFromHeader(request);
        tokenBlacklistService.addToBlacklist(accessToken);

    }

    // 데이터 변경
    @Transactional
    public void update(User user, UpdateRequestDto dto) {
        // 유저 최신화(컨트롤러에서 받아온 User는 영속성 컨텍스트의 보호를 받지 않음)
        User findUser = userRepository.findById(user.getId()).orElseThrow(
                ()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (hasText(dto.nickname()) && !dto.nickname().equals(findUser.getNickname())) {
            boolean nicknameExists = userRepository.existsByNickname(dto.nickname());
            if (nicknameExists) {
                throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
            }
        }

        String currentPassword = dto.currentPassword();
        String newPassword = dto.newPassword();

        if (hasText(currentPassword) && hasText(newPassword)) {
            // 현재 비밀번호가 일치하지 않으면 예외
            if (!passwordEncoder.matches(currentPassword, findUser.getPassword())) {
                throw new BusinessException(ErrorCode.INVALID_PASSWORD);
            }

            // 동일한 비밀번호로 변경하려는 경우도 방지
            if (passwordEncoder.matches(newPassword, findUser.getPassword())) {
                throw new BusinessException(ErrorCode.SAME_AS_OLD_PASSWORD);
            }

            newPassword = passwordEncoder.encode(newPassword);
        }

        findUser.update(
                newPassword,
                dto.name(),
                dto.nickname(),
                dto.phoneNumber()
        );
        userRepository.save(findUser);
    }

    // 회원 탈퇴
    @Transactional
    public void withdraw(User user) {
        // 유저 최신화
        User findUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        findUser.deleteRefreshToken();
        findUser.delete(user.getId());
        userRepository.save(findUser);
    }

}
