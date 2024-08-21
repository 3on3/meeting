package com.project.api.metting.service;

import com.project.api.metting.entity.User;
import com.project.api.metting.entity.UserProfile;
import com.project.api.metting.repository.UserProfileRepository;
import com.project.api.metting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadService {

    private final AwsS3Service s3Service;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    // 기본 이미지 URL
    private static final String DEFAULT_PROFILE_IMAGE_URL = "https://spring-file-bucket-yocong.s3.ap-northeast-2.amazonaws.com/2024/default_profile.png";

    /**
     * 파일 업로드 처리
     * @param profileImage - 클라이언트가 전송한 파일 바이너리 객체
     * @param userId - 사용자 ID
     * @return - 업로드된 파일의 URL
     */
    public String uploadProfileImage(MultipartFile profileImage, String userId) throws IOException {

        // 파일명을 유니크하게 변경
        String uniqueFileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();

        // 파일을 S3 버킷에 저장
        String url = s3Service.uploadToS3Bucket(profileImage.getBytes(), uniqueFileName);
        log.info("Uploaded file URL: {}", url);

        // 사용자 찾기
        User findUser = userRepository.findById(userId).orElseThrow(() -> new IOException("User not found"));

        // UserProfile 찾기
        UserProfile userProfile = userProfileRepository.findByUserId(findUser.getId());
        System.out.println("===================" + userProfile);

        if (userProfile == null) {
            // UserProfile이 없으면 새로 생성
            userProfile = UserProfile.builder()
                    .user(findUser)
                    .profileImg(url)  // 초기 프로필 이미지 설정
                    .build();
            log.info("Created new UserProfile: {}", userProfile);
        } else {
            // UserProfile이 존재하면 업데이트
            userProfile.setProfileImg(url);
            log.info("Updated UserProfile: {}", userProfile);
        }

        // UserProfile 저장
        userProfileRepository.save(userProfile);

        return url;
    }


    public String getDefaultProfileImage(String userId) {
        // 사용자 찾기
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // UserProfile 찾기
        UserProfile userProfile = userProfileRepository.findByUserId(findUser.getId());

        if (userProfile == null) {
            // UserProfile이 없으면 새로 생성하고 기본 이미지로 설정
            userProfile = UserProfile.builder()
                    .user(findUser)
                    .profileImg(DEFAULT_PROFILE_IMAGE_URL) // 기본 프로필 이미지 설정
                    .build();
            log.info("Created new UserProfile with default image: {}", userProfile);
        } else {
            // UserProfile이 존재하면 기본 이미지로 업데이트
            userProfile.setProfileImg(DEFAULT_PROFILE_IMAGE_URL);
            log.info("Updated UserProfile to default image: {}", userProfile);
        }

        // UserProfile 저장
        userProfileRepository.save(userProfile);

        return DEFAULT_PROFILE_IMAGE_URL;
    }

}
