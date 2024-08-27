//package com.project.api.metting.service;
//
//import com.project.api.metting.dto.request.ChangePasswordDto;
//import com.project.api.metting.dto.response.UserMyPageDto;
//import com.project.api.metting.entity.Membership;
//import com.project.api.metting.entity.User;
//import com.project.api.metting.entity.UserProfile;
//import com.project.api.metting.repository.UserMyPageRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder; // PasswordEncoder import 추가
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDate;
//import java.time.Period;
//import java.time.ZoneId;
//import java.util.Date;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class UserMyPageServiceTest {
//
//    @Mock
//    private UserMyPageRepository userMyPageRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder; // PasswordEncoder 모의(mock) 추가
//
//    @InjectMocks
//    private UserMyPageService userMyPageService;
//
//    @Test
//    void testGetUserInfo() {
//        // Given
//        String userId = "1";
//        LocalDate birthLocalDate = LocalDate.of(1990, 1, 1);
//        Date birthDate = Date.from(birthLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//        UserProfile userProfile = UserProfile.builder()
//                .profileImg("프사")
//                .profileIntroduce("안뇽")
//                .build();
//
//        User user = User.builder()
//                .id(userId)
//                .password("1234")
//                .birthDate(birthDate)
//                .phoneNumber("010-123-4567")
//                .major("컴공")
//                .nickname("최강미녀")
//                .userProfile(userProfile)
//                .membership(Membership.PREMIUM)
//                .build();
//
//        userProfile.setUser(user);
//
//        when(userMyPageRepository.findById(userId)).thenReturn(Optional.of(user));
//
//        // When
//        Optional<UserMyPageDto> result = userMyPageService.getUserInfo(userId);
//
//        // Then
//        assertEquals(true, result.isPresent());
//        assertEquals("1234", result.get().getPassword());
//        assertEquals("010-123-4567", result.get().getPhoneNumber());
//        assertEquals("컴공", result.get().getMajor());
//        assertEquals("최강미녀", result.get().getNickname());
//        assertEquals("프사", result.get().getProfileImg());
//        assertEquals("안뇽", result.get().getProfileIntroduce());
//        assertEquals(Membership.PREMIUM, result.get().getMembership());
//
//        int expectedAge = Period.between(birthLocalDate, LocalDate.now()).getYears();
//        assertEquals(expectedAge, result.get().getAge());
//    }
//
//    @Test
//    void testGetUserInfo_UserNotFound() {
//        // Given
//        String userId = "123";
//
//        when(userMyPageRepository.findById(userId)).thenReturn(Optional.empty());
//
//        // When
//        Optional<UserMyPageDto> result = userMyPageService.getUserInfo(userId);
//
//        // Then
//        assertEquals(false, result.isPresent());
//    }
//
//    @Test
//    void testChangePassword_Success() {
//        // Given
//        String userId = "1";
//        String currentPassword = "currentPassword";
//        String newPassword = "newPassword";
//        String confirmNewPassword = "newPassword";
//
//        ChangePasswordDto changePasswordDto = ChangePasswordDto.builder()
//                .currentPassword(currentPassword)
//                .newPassword(newPassword)
//                .confirmNewPassword(confirmNewPassword)
//                .build();
//
//        User user = User.builder()
//                .id(userId)
//                .password(passwordEncoder.encode(currentPassword)) // passwordEncoder를 사용하여 현재 비밀번호를 인코딩
//                .build();
//
//        when(userMyPageRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches(currentPassword, user.getPassword())).thenReturn(true);
//        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
//
//        // When
//        boolean result = userMyPageService.changePassword(userId, changePasswordDto);
//
//        // Then
//        assertTrue(result);
//        verify(userMyPageRepository, times(1)).save(user);
//        assertEquals("encodedNewPassword", user.getPassword()); // 인코딩된 새로운 비밀번호 확인
//    }
//
//    @Test
//    void testChangePassword_CurrentPasswordMismatch() {
//        // Given
//        String userId = "1";
//        String currentPassword = "currentPassword";
//        String newPassword = "newPassword";
//        String confirmNewPassword = "newPassword";
//
//        ChangePasswordDto changePasswordDto = ChangePasswordDto.builder()
//                .currentPassword(currentPassword)
//                .newPassword(newPassword)
//                .confirmNewPassword(confirmNewPassword)
//                .build();
//
//        User user = User.builder()
//                .id(userId)
//                .password(passwordEncoder.encode("wrongPassword"))
//                .build();
//
//        when(userMyPageRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches(currentPassword, user.getPassword())).thenReturn(false);
//
//        // When
//        boolean result = userMyPageService.changePassword(userId, changePasswordDto);
//
//        // Then
//        assertFalse(result);
//        verify(userMyPageRepository, times(0)).save(user);
//    }
//
//    @Test
//    void testChangePassword_NewPasswordMismatch() {
//        // Given
//        String userId = "1";
//        String currentPassword = "currentPassword";
//        String newPassword = "newPassword";
//        String confirmNewPassword = "differentNewPassword";
//
//        ChangePasswordDto changePasswordDto = ChangePasswordDto.builder()
//                .currentPassword(currentPassword)
//                .newPassword(newPassword)
//                .confirmNewPassword(confirmNewPassword)
//                .build();
//
//        User user = User.builder()
//                .id(userId)
//                .password(passwordEncoder.encode(currentPassword)) // passwordEncoder를 사용하여 현재 비밀번호를 인코딩
//                .build();
//
//        when(userMyPageRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches(currentPassword, user.getPassword())).thenReturn(true);
//
//        // When
//        boolean result = userMyPageService.changePassword(userId, changePasswordDto);
//
//        // Then
//        assertFalse(result);
//        verify(userMyPageRepository, times(0)).save(user);
//    }
//
//    @Test
//    void testChangePassword_UserNotFound() {
//        // Given
//        String userId = "123";
//        ChangePasswordDto changePasswordDto = ChangePasswordDto.builder()
//                .currentPassword("currentPassword")
//                .newPassword("newPassword")
//                .confirmNewPassword("newPassword")
//                .build();
//
//        when(userMyPageRepository.findById(userId)).thenReturn(Optional.empty());
//
//        // When
//        boolean result = userMyPageService.changePassword(userId, changePasswordDto);
//
//        // Then
//        assertFalse(result);
//        verify(userMyPageRepository, times(0)).save(any(User.class));
//    }
//}
