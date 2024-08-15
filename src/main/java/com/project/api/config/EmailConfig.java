//package com.project.api.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//
//import java.util.Properties;
//
//@Configuration  // 이 클래스가 스프링 설정 클래스임을 나타냅니다.
//public class EmailConfig {
//
//    // application.properties 또는 application.yml 파일에서 해당 값을 주입받습니다.
//    @Value("${spring.mail.host}")
//    private String host;
//
//    @Value("${spring.mail.port}")
//    private int port;
//
//    @Value("${spring.mail.username}")
//    private String username;
//
//    @Value("${spring.mail.password}")
//    private String password;
//
//    @Value("${spring.mail.properties.mail.smtp.auth}")
//    private boolean auth;
//
//    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
//    private boolean starttlsEnable;
//
//    @Value("${spring.mail.properties.mail.smtp.starttls.required}")
//    private boolean starttlsRequired;
//
//    @Value("${spring.mail.properties.mail.smtp.connectiontimeout}")
//    private int connectionTimeout;
//
//    @Value("${spring.mail.properties.mail.smtp.timeout}")
//    private int timeout;
//
//    @Value("${spring.mail.properties.mail.smtp.writetimeout}")
//    private int writeTimeout;
//
//    @Bean  // 이 메서드가 스프링 빈을 생성함을 나타냅니다.
//    public JavaMailSender javaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        // 메일 서버의 호스트를 설정합니다.
//        mailSender.setHost(host);
//        // 메일 서버의 포트를 설정합니다.
//        mailSender.setPort(port);
//        // 메일 서버 인증에 사용할 사용자 이름을 설정합니다.
//        mailSender.setUsername(username);
//        // 메일 서버 인증에 사용할 비밀번호를 설정합니다.
//        mailSender.setPassword(password);
//        // 기본 인코딩을 UTF-8로 설정합니다.
//        mailSender.setDefaultEncoding("UTF-8");
//        // 추가적인 메일 서버 속성을 설정합니다.
//        mailSender.setJavaMailProperties(getMailProperties());
//
//        return mailSender;
//    }
//
//    // 메일 서버에 대한 추가적인 속성들을 설정하는 메서드입니다.
//    private Properties getMailProperties() {
//        Properties properties = new Properties();
//        // SMTP 인증을 사용할지 여부를 설정합니다.
//        properties.put("mail.smtp.auth", auth);
//        // TLS를 사용할지 여부를 설정합니다.
//        properties.put("mail.smtp.starttls.enable", starttlsEnable);
//        // TLS가 필수인지 여부를 설정합니다.
//        properties.put("mail.smtp.starttls.required", starttlsRequired);
//        // SMTP 연결 타임아웃 시간을 설정합니다.
//        properties.put("mail.smtp.connectiontimeout", connectionTimeout);
//        // SMTP 응답 타임아웃 시간을 설정합니다.
//        properties.put("mail.smtp.timeout", timeout);
//        // SMTP 쓰기 타임아웃 시간을 설정합니다.
//        properties.put("mail.smtp.writetimeout", writeTimeout);
//
//        return properties;
//    }
//}
//
