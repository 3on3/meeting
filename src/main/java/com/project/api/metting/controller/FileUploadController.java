package com.project.api.metting.controller;


import com.project.api.auth.TokenProvider;
import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.request.UserRegisterDto;
import com.project.api.metting.entity.User;
import com.project.api.metting.repository.UserRepository;
import com.project.api.metting.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService uploadService;
    private final UserRepository userRepository;

    // 파일 업로드 처리
    @PostMapping("/file/upload")
    public ResponseEntity<?> upload(
            @RequestPart(value = "profileImage") MultipartFile uploadFile,
            @AuthenticationPrincipal TokenUserInfo tokenInfo
    ) {

        log.info("profileImage: {}", uploadFile.getOriginalFilename());

        // 파일을 업로드
        String fileUrl = "";
        try {
            fileUrl = uploadService.uploadProfileImage(uploadFile, tokenInfo.getUserId());
        } catch (IOException e) {
            log.warn("파일 업로드에 실패했습니다.");
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().body(fileUrl);
    }
}

