package com.sparta.delivery.user.service;

import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.security.JwtUtil;
import com.sparta.delivery.user.domain.User;
import com.sparta.delivery.user.dto.*;
import com.sparta.delivery.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    //암호화 후 db에 회원가입 정보 저장
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
        Optional<User> findUser = userRepository.findByEmail(dto.email());

        if (findUser.isEmpty()) {  //이메일이 존재하지 않다 반환 시 찾을 때까지 이메일 무한 입력 가능성이 있으니 404 반환
            throw new BusinessException(ErrorCode.LOGIN_USER_NOT_FOUND); //404
        }

        User user = findUser.get();

        // 입력된 비밀번호, 저장된 비밀번호 비교
        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_USER_NOT_FOUND);  //404
        }

        jwtUtil.issueAndSetAccessToken(response, user.getEmail());
        jwtUtil.issueAndSetRefreshToken(response, user);

    }

    // 핸드폰 번호 변경 (바로 변경)
    @Transactional
    public void updatePhoneNumber(User user, @Valid UpdatePhoneNumberDto dto) {
        user.updatePhoneNumber(dto.phoneNumber());
        userRepository.save(user);
    }

    // 닉네임 변경 (중복 검사)
    @Transactional
    public void updateNickname(User user, @Valid UpdateNicknameDto dto) {
        // 닉네임 중복 검사
        if (userRepository.existsByNickname(dto.nickname())) {
            throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        user.updateNickname(dto.nickname());
        userRepository.save(user);
    }

    // 현재 비밀번호 확인
    public void verifyPassword(User user, @Valid VerifyPasswordDto dto) {
        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
    }

    // 패스워드 변경 (재확인)
    @Transactional
    public void updatePassword(User user, @Valid UpdatePasswordDto dto) {

        // 새 비밀번호 재확인
        if (!dto.confirmPassword().equals(dto.newPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_CONFIRM_NOT_MATCH);
        }

        user.updatePassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    // 회원 탈퇴
    @Transactional
    public void withdraw(User user) {
        User findUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        findUser.delete(user.getId());

    }


}
