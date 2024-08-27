package com.project.api.metting.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@Service
@Slf4j
public class AwsS3Service {

    private S3Client s3;

    // AWS 접근 키
    @Value("${aws.credentials.accessKey}")
    private String accessKey;

    // AWS 비밀 키
    @Value("${aws.credentials.secretKey}")
    private String secretKey;

    // AWS 리전 (예: ap-northeast-2)
    @Value("${aws.region}")
    private String region;

    // S3 버킷 이름
    @Value("${aws.bucketName}")
    private String bucketName;


    // 기본 이미지의 URL을 반환
    //★ 기본 이미지 URL (application.yml에 정의된 경로)
    @Getter
    @Value("${default.profile.image.url}")
    private String defaultImageUrl;

    /**
     * S3 클라이언트를 초기화하는 메서드.
     * 애플리케이션이 시작될 때 @PostConstruct 어노테이션을 통해 자동으로 호출됩니다.
     */
    @PostConstruct
    private void initAmazonS3() {
        // AWS 자격 증명 생성
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // S3 클라이언트 생성 및 초기화
        this.s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    /**
     * S3 버킷에 파일을 업로드하고 업로드된 파일의 URL을 반환하는 메서드.
     *
     * @param uploadFile - 파일의 바이너리 데이터
     * @param fileName - S3에 저장할 파일명
     * @return - 저장된 파일의 S3 URL
     */
    public String uploadToS3Bucket(byte[] uploadFile, String fileName) {

        // 현재 날짜를 기반으로 폴더 경로 생성 (예: 2024/08/21)
        String datePath = LocalDate.now().toString().replace("-", "/");

        // 폴더 경로와 파일명을 결합하여 S3 저장 경로 생성
        String fullPath = datePath + "/" + fileName;

        // S3 버킷에 파일 업로드를 위한 요청 생성
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName) // 버킷명 설정
                .key(fullPath)      // 파일 경로 설정
                .build();

        // S3 버킷에 파일 업로드
        s3.putObject(request, RequestBody.fromBytes(uploadFile));

        // 업로드된 파일의 URL을 반환
        return s3.utilities()
                .getUrl(b -> b.bucket(bucketName).key(fullPath)) // fullPath를 사용하여 S3 URL 생성
                .toString();
    }


}
