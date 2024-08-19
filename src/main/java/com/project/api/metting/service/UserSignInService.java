package com.project.api.metting.service;

import com.project.api.auth.TokenProvider;
import com.project.api.exception.LoginFailException;
import com.project.api.metting.dto.request.LoginRequestDto;
import com.project.api.metting.dto.response.LoginResponseDto;
import com.project.api.metting.entity.Membership;
import com.project.api.metting.entity.User;
import com.project.api.metting.repository.UserMembershipRepositoryCustom;
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

    private final UserRepository userRepository;

    @Qualifier("userMembershipRepositoryCustomImpl")
    private final UserMembershipRepositoryCustom userMembershipRepositoryCustom;

    //토큰 생성 객체
    private final TokenProvider tokenProvider;

    // 패스워드 암호화 객체
    private final PasswordEncoder encoder;

    //인증 처리
    public LoginResponseDto authenticate(LoginRequestDto dto, boolean rememberMe) {

        // 아이디를 통해 회원정보 조회
        User userInfo = userRepository
                .findByEmail(dto.getEmail())
                .orElseThrow(
                        () -> new LoginFailException("User not found")
                );

        // 탈퇴 여부 확인
        if (userInfo.isWithdrawn()) {
            throw new LoginFailException("Account has been withdrawn");
        }

        // 패스워드를 설정하지 않은 회원
        if (userInfo.getPassword() == null) {
            throw new LoginFailException("User not found");
        }

        // 패스워드 검증
        String inputPassword = dto.getPassword();
        String encodedPassword = userInfo.getPassword();

        if (!encoder.matches(inputPassword, encodedPassword)) {
            throw new LoginFailException("Invalid password");
        }

        // 로그인 성공시
        // 인증정보(이메일, 닉네임, 프로필, 토큰정보)를 클라이언트에게 전송

        // 토큰 생성
        String token = tokenProvider.createToken(userInfo);

//        Membership membershipAuth = userMembershipRepositoryCustom.membershipAuthFind(userInfo.getEmail());


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
                .isWithdrawn(userInfo.isWithdrawn())
                .password(userInfo.getPassword())
                .membershipAuth(userInfo.getMembership())
                .build();

        log.info("로그인 토큰 전달하기",responseDto);

        // 자동로그인이라면?
        // -> 리프레쉬 토큰 생성
        if (rememberMe) {
            String refreshToken = tokenProvider.createRefreshToken();
            saveRefreshToken(userInfo, refreshToken); // 사용자와 함께 저장

            responseDto.setRefreshToken(refreshToken);
            log.info("refreshToken info : {}", refreshToken);
        }
        return responseDto;
    }

    // 리프레시 토큰을 생성하고 업데이트하고, DB에 저장하는 메서드
    public void saveRefreshToken(User user, String refreshToken) {
        Instant expiryDate = Instant.now().plusMillis(tokenProvider.getRefreshTokenExpirationDays() * 24 * 60 * 60 * 1000);
        user.updateRefreshToken(refreshToken, expiryDate);
        userRepository.save(user);
    }

    public LoginResponseDto refreshAccessToken(String email, String refreshToken) {
        User user = userRepository.findByEmailAndRefreshToken(email, refreshToken)
                .orElseThrow(() -> new LoginFailException("유효하지 않은 리프레시 토큰입니다."));

        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            throw new LoginFailException("리프레시 토큰이 만료되었습니다.");
        }

        String newAccessToken = tokenProvider.createToken(user);

        return LoginResponseDto.builder()
                .email(user.getEmail())
                .auth(user.getAuth().toString())
                .token(newAccessToken)
                .build();
    }
}
