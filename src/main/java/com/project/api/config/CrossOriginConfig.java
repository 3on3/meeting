package com.project.api.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//전역 크로스 오리진 설정 : 어떤 클라이언트를 허용할 것인지
@Configuration
public class CrossOriginConfig implements WebMvcConfigurer {

    private String[] url = {
            "http://localhost:3000",
            "http://localhost:3001",
            "http://localhost:3002",
            "https://gwating.com",
            "https://www.gwating.com",
            "https://d1jw85cmg05158.cloudfront.net",
            "https://3.38.26.248"

    };
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOrigins(url) // dㅓ떤 클라이언트를
                .allowedMethods("*") //어떤 방식에서
                .allowedHeaders("*") //어떤 헤더를 허용할지
                .allowCredentials(true) // 쿠키 전송을 허용할지
                .exposedHeaders("Authorization") // 예시: 클라이언트에 노출할 헤더 추가
                .maxAge(3600L); // Pre-flight 요청의 캐싱 시간(초)
        ;
    }
}
