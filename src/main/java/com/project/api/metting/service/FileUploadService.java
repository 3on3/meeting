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

}
