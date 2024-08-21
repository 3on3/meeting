package com.project.api.metting.service;

import com.project.api.auth.TokenProvider;
import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.exception.DuplicateNicknameException;
import com.project.api.metting.dto.request.*;
import com.project.api.metting.dto.response.UserMyPageDto;
import com.project.api.metting.entity.*;
import com.project.api.metting.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.patterns.HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;
import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMyPageService {

    @Value("${study.mail.host}")
    private String mailHost;
    private String profileImageUploadDir;
    private final UserMyPageRepository userMyPageRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserVerificationRepository userVerificationRepository;
    // 이메일 전송 객체
    private final JavaMailSender mailSender;

    private final AwsS3Service awsS3Service;




        /**
         * 주어진 사용자 ID에 해당하는 유저 정보
         *
         * @param userId - 조회할 사용자의 ID
         * @return UserMyPageDto - 사용자 정보가 담긴 DTO
         * @throws IllegalArgumentException - 사용자를 찾을 수 없는 경우 예외 발생
         */
    public UserMyPageDto getUserInfo(String userId) {
        User user = userMyPageRepository.findById(userId)
                                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return convertToDto(user);
    }

    private UserMyPageDto convertToDto(User user) {

         return UserMyPageDto.builder()
                .profileIntroduce(user.getUserProfile() != null && user.getUserProfile().getProfileIntroduce() != null
                        ? user.getUserProfile().getProfileIntroduce()
                        : "소개가 없습니다.")
                .profileImg(user.getUserProfile().getProfileImg())
                .nickname(user.getNickname())
                .membership(user.getMembership() != null ? user.getMembership() : Membership.GENERAL)
                .age(calculateAge(user.getBirthDate()))
                .univ(user.getUnivName())
                .major(user.getMajor())
                .build();
    }

    /**
     * 사용자의 생년월일을 바탕으로 나이 계산
     *
     * @param birthDate - 사용자의 생년월일 (Date 형식)
     * @return int - 계산된 사용자 나이
     */
    private int calculateAge(Date birthDate) {
        LocalDate birthLocalDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Period.between(birthLocalDate, LocalDate.now()).getYears();
    }


    /**
     * 사용자 정보를 업데이트
     *
     * @param userId    - 업데이트할 사용자의 ID
     * @param updateDto - 업데이트할 사용자 정보가 포함된 DTO
     * @return 업데이트된 사용자 정보가 담긴 UserMyPageDto
     * @throws IllegalArgumentException    - 유효하지 않은 사용자 ID일 경우 예외 발생
     * @throws DuplicateNicknameException - 닉네임이 중복될 경우 예외 발생
     */
    public UserMyPageDto updateUserFields(String userId, UserUpdateRequestDto updateDto) {
        // 사용자 조회
        User user = userMyPageRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다: " + userId));

        // 닉네임 중복시 에러 메시지 반환
        if (updateDto.getNickname() != null && !updateDto.getNickname().equals(user.getNickname())) {
            if (userMyPageRepository.findByNickname(updateDto.getNickname()).isPresent()) {
                throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다");
            }
            user.setNickname(updateDto.getNickname());
        }
        if (updateDto.getMembership() != null) {
            Membership membership = Membership.valueOf(updateDto.getMembership().toUpperCase());
            user.setMembership(membership);
        }
        if (updateDto.getUniv() != null) {
            user.setUnivName(updateDto.getUniv());
        }
        if (updateDto.getMajor() != null) {
            user.setMajor(updateDto.getMajor());
        }
        if (updateDto.getProfileIntroduce() != null) {
            UserProfile userProfile = user.getUserProfile();

        if (userProfile == null) {
            userProfile = new UserProfile();
        }
        // profileIntroduce 값을 설정
        userProfile.setProfileIntroduce(updateDto.getProfileIntroduce());

        // User 객체에 UserProfile 설정
        user.setUserProfile(userProfile);
        }
        userMyPageRepository.save(user);
        return convertToUserMyPageDto(user);
    }

    /**
     * 사용자의 프로필 정보를 조회
     *
     * @param userId - 조회할 사용자의 ID
     * @return UserProfile - 사용자의 프로필 정보, 프로필이 없거나 유효하지 않은 경우 null 반환
     */
    public UserProfile getUserProfile(String userId) {
        // 유저 ID로 User 객체를 조회
        Optional<User> user = userRepository.findById(userId);
        // 조회한 User 객체와 연결된 UserProfile을 반환
        return user.map(userProfileRepository::findByUser).orElse(null);
    }



    // User 엔티티를 UserMyPageDto로 변환
    public UserMyPageDto convertToUserMyPageDto(User user) {
        log.info("Converting updated user entity to DTO for user: {}", user.getNickname());
        UserMyPageDto dto = new UserMyPageDto();

        dto.setNickname(user.getNickname());
        dto.setMembership(user.getMembership());
        dto.setUniv(user.getUnivName());
        dto.setMajor(user.getMajor());

        // 나이 계산
        if (user.getBirthDate() != null) {
            dto.setAge(calculateAge(user.getBirthDate()));
        }
        return dto;
    }



    /**
     * 사용자의 비밀번호를 변경
     *
     * @param userId             - 비밀번호를 변경할 사용자의 ID
     * @param changePasswordDto  - 비밀번호 변경에 필요한 정보가 담긴 DTO
     * @throws IllegalArgumentException - 비밀번호 변경 데이터가 null이거나, 비밀번호가 일치하지 않을 경우 발생
     */
    public void changePassword(String userId, ChangePasswordDto changePasswordDto) {

        if (changePasswordDto == null) {
            throw new IllegalArgumentException("비밀번호 변경 데이터는 null일 수 없습니다");
        }

        User user = userMyPageRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID로 사용자를 찾을 수 없습니다: " + userId));

        if (changePasswordDto.getNewPassword() == null ||
                changePasswordDto.getConfirmNewPassword() == null ||
                !changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않거나 null입니다");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userMyPageRepository.save(user);
    }


    /**
     * 인증 코드를 생성하고, 해당 정보를 데이터베이스에 저장
     *
     * @param email - 인증 코드를 전송할 이메일 주소
     * @param user  - 인증 코드를 생성할 사용자 정보
     * @param code  - 생성된 인증 코드
     */
    @Transactional
    private void generateAndSaveCode(String email, User user, String code) {

        // 인증 코드 정보를 데이터베이스에 저장
        UserVerification temporaryVerification = UserVerification.builder()
                .user(user)
                .email(email)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .verificationCode(code)
                .build();

        userVerificationRepository.save(temporaryVerification);
    }


    /**
     * 사용자에게 인증 이메일을 전송하고, 생성된 인증 코드를 반환
     *
     * @param email - 인증 이메일을 보낼 사용자 이메일 주소
     * @return 생성된 인증 코드
     * @throws IllegalArgumentException - 사용자를 찾을 수 없을 경우 발생
     * @throws RuntimeException - 이메일 전송 중 오류가 발생할 경우 발생
     */
    @Transactional
    public String sendVerificationEmail(String email) {
        log.info("email send info - {}", email);

        User findUser = userMyPageRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 검증 코드 생성하기
        String code = generateVerificationCode();

        // 이메일을 전송할 객체 생성
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            // 누구에게 이메일을 보낼 것인지
            messageHelper.setTo(email);
            // 이메일 제목 설정
            messageHelper.setSubject("[인증메일] 과팅 가입 인증 메일입니다.");
            // 이메일 내용 설정
            messageHelper.setText(
                    "인증 코드: <b style=\"font-weight: 700; letter-spacing: 5px; font-size: 30px;\">" + code + "</b>"
                    , true
            );

            // 전송자의 이메일 주소
            messageHelper.setFrom(mailHost);

            // 이메일 보내기
            mailSender.send(mimeMessage);

            // 이메일 전송 후 인증 코드를 저장
            generateAndSaveCode(email, findUser, code);

            return code;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 검증 코드 생성 로직 1000~9999 사이의 4자리 숫자
    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 9000 + 1000));
    }

    // 인증코드 체크 : 인증코드가 있고 만료시간이 지나지 않았고 코드번호가 일치하는 경우
    public boolean isMatchCode(String email, String code) {

        // 이메일을 통해 회원정보를 탐색
        User user = userRepository.findByEmail(email)
                .orElse(null);
        if (user != null) {
            // 인증코드가 있는지 탐색
            UserVerification ev = userVerificationRepository.findByUser(user)
                    .orElse(null);
            // 인증코드가 있고 만료시간이 지나지 않았고 코드번호가 일치할 경우
            if (ev != null
                    && ev.getExpiryDate().isAfter(LocalDateTime.now())
                    && code.equals(ev.getVerificationCode())
            ) {
                return true;
            }
        }
        return false;
    }

    /**
     * 사용자가 입력한 인증 코드를 검증
     *
     * @param verificationDto - 인증 코드와 이메일이 포함된 DTO
     * @return 인증이 성공적으로 이루어진 경우 true, 실패한 경우 예외 발생
     * @throws IllegalArgumentException - 인증 코드가 틀리거나 만료된 경우 발생
     */
    @Transactional
    public boolean verifySendingCode(TemporaryVerficationDto verificationDto) {
        // 이메일을 통해 회원정보를 탐색
        User findUser = userRepository.findByEmail(verificationDto.getEmail()).orElse(null);
        if (findUser != null) {
            // 인증코드가 있는지 탐색
            UserVerification ev = userVerificationRepository.findByUser(findUser).orElse(null);
            // 인증코드가 있고, 만료시간이 지나지 않았고 코드번호가 일치할 경우
            if (ev != null
                    && ev.getExpiryDate().isAfter(LocalDateTime.now())
                    && verificationDto.getCode().equals(ev.getVerificationCode())) {
                // 인증코드 데이터베이스에서 삭제
                userVerificationRepository.delete(ev);
                return true;
            } else {
                // 인증코드가 틀렸거나 만료된 경우
                // 기존 인증코드 삭제
                if (ev != null) {
                    userVerificationRepository.delete(ev);
                }
                // 인증 실패에 대한 명확한 메시지 제공
                throw new IllegalArgumentException("인증 코드가 틀리거나 만료되었습니다. 새로운 인증 코드가 이메일로 전송되었습니다.");
            }
        }
        return false;
    }

    /**
     * 사용자가 입력한 비밀번호를 검증
     *
     * @param verificationDto - 이메일과 비밀번호가 포함된 DTO
     * @return 입력된 비밀번호가 저장된 비밀번호와 일치하면 true, 그렇지 않으면 false
     * @throws IllegalArgumentException - 유효하지 않은 이메일 또는 비밀번호일 경우 예외 발생
     */
    public boolean verifyPassword(PasswordVerificationDto verificationDto) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(verificationDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // 입력된 비밀번호와 저장된 비밀번호 해시값 비교
        if (passwordEncoder.matches(verificationDto.getPassword(), user.getPassword())) {
            return true;
        }
        return false;
    }


    /**
     * 사용자의 이메일과 입력된 비밀번호를 검증
     *
     * @param email       - 검증할 사용자의 이메일
     * @param rawPassword - 사용자가 입력한 비밀번호 (평문)
     * @return 입력된 비밀번호가 저장된 암호화된 비밀번호와 일치하면 true, 그렇지 않으면 false
     * @throws IllegalArgumentException - 해당 이메일로 사용자를 찾을 수 없는 경우 예외
     */

    public boolean checkPassword(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 입력한 비밀번호와 DB의 암호화된 비밀번호를 비교
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    /**
     * 사용자의 전화번호를 업데이트
     *
     * @param email - 전화번호를 업데이트할 사용자의 이메일
     * @param dto   - 새로운 전화번호가 포함된 DTO
     * @throws IllegalArgumentException - 전화번호 데이터가 null이거나, 이미 존재하는 번호일 경우 또는 사용자를 찾을 수 없는 경우 발생
     */

    public void updatePhoneNumber(String email, UpdatePhoneNumberDto dto) {
        if (dto == null || dto.getPhoneNumber() == null) {
            throw new IllegalArgumentException("전화번호 데이터는 null일 수 없습니다");
        }
        boolean phoneExists = userMyPageRepository.existsByPhoneNumber(dto.getPhoneNumber());
        if (phoneExists) {
            throw new IllegalArgumentException("이미 존재하는 번호입니다.");
        }

        User user = userMyPageRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 email로 사용자를 찾을 수 없습니다: " + email));

        user.setPhoneNumber(dto.getPhoneNumber());
        userMyPageRepository.save(user);
    }

    /**
     * 사용자의 계정을 탈퇴 처리
     *
     * @param email          - 탈퇴 처리할 사용자의 이메일
     * @param tokenUserInfo  - 현재 로그인된 사용자 정보
     * @throws IllegalArgumentException - 사용자를 찾을 수 없는 경우 발생
     */
    public void withDrawnUser(String email, TokenUserInfo tokenUserInfo) {
        User findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        findUser.setIsWithdrawn(true);
        userRepository.save(findUser);
    }
}