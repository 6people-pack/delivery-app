package com.sparta.delivery.user.service;

import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.global.unit.utils.CookieUtils;
import com.sparta.delivery.security.jwt.dto.RefreshTokenResponseDto;
import com.sparta.delivery.security.jwt.utils.JwtUtil;
import com.sparta.delivery.user.domain.User;
import com.sparta.delivery.user.dto.*;
import com.sparta.delivery.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원 가입
    @Transactional
    public void signup(@Valid SignUpRequestDto RequestDto) {
        if (userRepository.findByEmail(RequestDto.email()).isPresent()) { // 이메일 중복
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);  //409
        }

        userRepository.save(User.createCustomer(
                RequestDto.email(),
                passwordEncoder.encode(RequestDto.password()),
                RequestDto.nickname(),
                RequestDto.phoneNumber()
        ));
    }

    //로그인
    @Transactional
    public void login(@Valid LoginRequestDto dto, HttpServletResponse response) {

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

        if (!findUser.getRefreshToken().equals(cookieRefreshToken)) {          // db에 있는 토큰과 비교
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
        // refreshToken은 http only 쿠키 방식으로 저장, ttlTime만큼 시간이 경과하면 삭제
        CookieUtils.setRefreshTokenCookie(response, newRefreshToken, ttlTime);

    }

    // 로그아웃
    @Transactional
    public void logout(HttpServletResponse response) {

        // Response 헤더에서 토큰 제거
        response.setHeader("Authorization", null);
        response.setHeader("Refresh-Token", null);

        // 쿠키에 있는 리프레시 토큰은 자동 로그인 기능 시 필요해서 상황에 따라 삭제하거나 말거나

        // 엑세스 토큰은 무효화가 안됨 로그아웃 후 살아있는 몇분 동안 위험할 수도
        // 블랙리스트방식이 있다는 것 같지만 시간 관계상 여유되면 구현

    }

    // 핸드폰 번호 변경 (바로 변경)
    @Transactional
    public void updatePhoneNumber(User user, @Valid UpdatePhoneNumberRequestDto dto) {
        // 유저 최신화(컨트롤러에서 받아온 User는 영속성 컨텍스트의 보호를 받지 않음)
        User findUser = userRepository.findById(user.getId()).orElseThrow(
                ()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        findUser.updatePhoneNumber(dto.phoneNumber());
        userRepository.save(findUser);
    }

    // 닉네임 변경 (중복 검사)
    @Transactional
    public void updateNickname(User user, @Valid UpdateNicknameRequestDto dto) {
        // 유저 최신화
        User findUser = userRepository.findById(user.getId()).orElseThrow(
                ()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 닉네임 중복 검사
        if (userRepository.existsByNickname(findUser.getNickname())) {
            throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        findUser.updateNickname(dto.nickname());
        userRepository.save(findUser);
    }

    // 현재 비밀번호 확인
    @Transactional(readOnly = true)
    public void verifyPassword(User user, @Valid VerifyPasswordRequestDto dto) {
        // 유저 최신화
        User findUser = userRepository.findById(user.getId()).orElseThrow(
                ()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(dto.currentPassword(), findUser.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
    }

    // 패스워드 변경 (재확인)
    @Transactional
    public void updatePassword(User user, @Valid UpdatePasswordRequestDto dto) {
        User findUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 새 비밀번호 재확인
        if (!dto.confirmPassword().equals(dto.newPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_CONFIRM_NOT_MATCH);
        }

        findUser.updatePassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(findUser);
    }

    // 회원 탈퇴
    @Transactional
    public void withdraw(User user) {
        // 유저 최신화
        User findUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        findUser.delete(user.getId());
        userRepository.save(findUser);
    }

}
