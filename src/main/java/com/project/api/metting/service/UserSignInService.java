package com.project.api.metting.service;

import com.project.api.auth.TokenProvider;
import com.project.api.exception.LoginFailException;
import com.project.api.metting.dto.request.LoginRequestDto;
import com.project.api.metting.dto.response.LoginResponseDto;
import com.project.api.metting.entity.User;
import com.project.api.metting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class UserSignInService {

    private final UserRepository userRepository;  // 사용자 데이터베이스 저장소

    private final TokenProvider tokenProvider;  // 토큰 생성 및 검증 서비스

    private final PasswordEncoder encoder;  // 비밀번호 암호화 서비스

    // 인증 처리
    public LoginResponseDto authenticate(LoginRequestDto dto, boolean rememberMe) {

        // 이메일로 사용자 정보 조회
        User userInfo = userRepository
                .findByEmail(dto.getEmail())
                .orElseThrow(
                        () -> new LoginFailException("User not found")  // 사용자 없을 경우 예외 처리
                );

        // 계정 탈퇴 여부 확인
        if (userInfo.getIsWithdrawn()) {
            throw new LoginFailException("Account has been withdrawn");  // 계정 탈퇴 시 예외 처리
        }

        // 비밀번호가 설정되어 있지 않은 경우
        if (userInfo.getPassword() == null) {
            throw new LoginFailException("User not found");  // 비밀번호 미설정 시 예외 처리
        }

        // 입력한 비밀번호와 저장된 비밀번호 비교
        String inputPassword = dto.getPassword();
        String encodedPassword = userInfo.getPassword();

        if (!encoder.matches(inputPassword, encodedPassword)) {
            throw new LoginFailException("Invalid password");  // 비밀번호 불일치 시 예외 처리
        }

        // 로그인 성공 시, 인증 정보와 토큰을 클라이언트에게 전송
        String token = tokenProvider.createToken(userInfo);  // 액세스 토큰 생성

        LoginResponseDto responseDto = LoginResponseDto.builder()
                .email(userInfo.getEmail())
                .auth(userInfo.getAuth().toString())
                .token(token)
                .name(userInfo.getName())
                .birthDate(String.valueOf(userInfo.getBirthDate()))
                .phoneNumber(userInfo.getPhoneNumber())
                .univName(userInfo.getUnivName())
                .major(userInfo.getMajor())
                .gender(userInfo.getGender())
                .nickname(userInfo.getNickname())
                .isWithdrawn(userInfo.getIsWithdrawn())
                .password(userInfo.getPassword())
                .membershipAuth(userInfo.getMembership())
                .profileImg(userInfo.getUserProfile().getProfileImg())
                .build();

        log.info("로그인 토큰 전달하기", responseDto);  // 로그인 토큰 전달 로그

        // 자동 로그인 설정 시
        if (rememberMe) {
            String refreshToken = tokenProvider.createRefreshToken();  // 리프레시 토큰 생성
            saveRefreshToken(userInfo, refreshToken);  // 리프레시 토큰과 함께 사용자 정보 저장

            responseDto.setRefreshToken(refreshToken);  // 리프레시 토큰 설정
            log.info("refreshToken info : {}", refreshToken);  // 리프레시 토큰 로그
        }
        return responseDto;  // 로그인 응답 반환
    }

    // 리프레시 토큰을 생성하고 사용자에 대해 업데이트하며 DB에 저장하는 메서드
    public void saveRefreshToken(User user, String refreshToken) {
        Instant expiryDate = Instant.now().plusMillis(tokenProvider.getRefreshTokenExpirationDays() * 24 * 60 * 60 * 1000);  // 리프레시 토큰 만료 날짜 계산
        user.updateRefreshToken(refreshToken, expiryDate);  // 사용자 정보에 리프레시 토큰 업데이트
        userRepository.save(user);  // DB에 저장
    }

    // 리프레시 토큰을 사용하여 새로운 액세스 토큰 생성
    public LoginResponseDto refreshAccessToken(String email, String refreshToken) {
        User user = userRepository.findByEmailAndRefreshToken(email, refreshToken)
                .orElseThrow(() -> new LoginFailException("유효하지 않은 리프레시 토큰입니다."));  // 유효하지 않은 리프레시 토큰 예외 처리

        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            throw new LoginFailException("리프레시 토큰이 만료되었습니다.");  // 리프레시 토큰 만료 시 예외 처리
        }

        String newAccessToken = tokenProvider.createToken(user);  // 새로운 액세스 토큰 생성

        return LoginResponseDto.builder()
                .email(user.getEmail())
                .auth(user.getAuth().toString())
                .token(newAccessToken)
                .build();  // 새로운 액세스 토큰을 포함한 응답 반환
    }
}
