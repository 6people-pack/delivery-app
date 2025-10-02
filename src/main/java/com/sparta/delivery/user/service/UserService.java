package com.sparta.delivery.user.service;

import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.global.unit.utils.CookieUtils;
import com.sparta.delivery.security.JwtUtil;
import com.sparta.delivery.user.domain.RefreshToken;
import com.sparta.delivery.user.domain.User;
import com.sparta.delivery.user.dto.LoginRequestDto;
import com.sparta.delivery.user.dto.RefreshTokenDto;
import com.sparta.delivery.user.dto.SignUpRequestDto;
import com.sparta.delivery.user.repository.RefreshTokenRepository;
import com.sparta.delivery.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;


    //암호화 후 db에 회원가입 정보 저장
    @Transactional
    public void signup(SignUpRequestDto RequestDto) {
        if (userRepository.findByEmail(RequestDto.email()).isPresent()) { // 이메일 중복
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);  //409
        }

//        // 확장성 생각하면 유저 엔티티 생성 추적이 힘들어질 가능성이 높아서, builder 말고 파라미터 많아도 user.create()로 하는게 나을 수 있다
//        User user = UserMapper.toUser(RequestDto, passwordEncoder);
        User user = User.create(
                RequestDto.email(),
                passwordEncoder.encode(RequestDto.password()),
                RequestDto.nickname(),
                RequestDto.phoneNumber()
        );
        userRepository.save(user);
    }


    //로그인
    @Transactional
    public void login(LoginRequestDto dto, HttpServletResponse response) {

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

        //TODO 이부분 코드 검증이랑 다 추가해야 합니다

        //        //가입된 정보가 일치하고 db에 refreshToken이 DB에 존재하고 있어야 하고 만약 기간 만료 시 재발급
//        // DB 조회
//        Optional<RefreshToken> dbRefreshToken = refreshTokenRepository.findRefreshTokenByUser(user);
//        // DB에 리프레시 토큰이 있다면 삭제
//        dbRefreshToken.ifPresent(refreshTokenRepository::delete);

        refreshTokenRepository.deleteByUser(user); // db에 리프레시 토큰 있으면 삭제

        // 만료 시간도 받아오기 위해 Dto로 전달
        RefreshTokenDto refreshTokenDto = jwtUtil.issueRefreshToken(user.getEmail());
        String refreshToken = refreshTokenDto.token();
        Date exp = refreshTokenDto.exp();

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .refreshToken(refreshToken)
                        .user(user)
                        .exp(exp)
                        .build()
        );

        String accessToken = jwtUtil.issueAccessToken(user.getEmail());

        Duration ttlTime = Duration.between(
                Instant.now(),
                exp.toInstant()
        );


        // accessToken은 헤더에 저장하고
        response.setHeader("Authorization", accessToken);
        // refreshToken은 http only 쿠키 방식으로 클라이언트에게 줌, ttlTime만큼 시간이 경과하면 삭제됨 ->이러면 db나 토큰에 만료 시간 설정 없어도 되나
        CookieUtils.setRefreshTokenCookie(response, refreshToken, ttlTime);

    }

}




