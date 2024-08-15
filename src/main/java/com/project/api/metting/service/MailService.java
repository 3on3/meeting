package com.project.api.metting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j  // 로깅 기능을 쉽게 사용하기 위해 Lombok의 @Slf4j 애너테이션을 사용합니다.
@Service  // 이 클래스가 스프링 서비스 컴포넌트임을 나타냅니다.
@Transactional  // 이 클래스의 모든 메서드가 트랜잭션 내에서 실행됨을 보장합니다.
@RequiredArgsConstructor  // final 필드에 대한 생성자를 Lombok이 자동으로 생성해줍니다.
public class MailService {

    // JavaMailSender를 통해 이메일을 발송하는 역할을 수행합니다. 생성자를 통해 주입됩니다.
    private final JavaMailSender emailSender;

    /**
     * 이메일을 발송하는 메서드입니다.
     *
     * @param toEmail 이메일 수신자 주소
     * @param title   이메일 제목
     * @param text    이메일 내용
     */
    public void sendEmail(String toEmail,
                          String title,
                          String text) {
        // 이메일 내용을 설정하는 메서드를 호출하여 SimpleMailMessage 객체를 생성합니다.
        SimpleMailMessage emailForm = createEmailForm(toEmail, title, text);
        try {
            // emailSender를 사용하여 이메일을 발송합니다.
            emailSender.send(emailForm);
        } catch (RuntimeException e) {
            // 이메일 발송 중 예외가 발생하면, 예외와 관련된 정보(수신자, 제목, 내용)를 로그로 남깁니다.
            log.debug("MailService.sendEmail exception occur toEmail: {}, " +
                    "title: {}, text: {}", toEmail, title, text);
            // 이메일 발송 실패 시, 사용자 정의 예외를 발생시켜 처리합니다.
//            throw new BusinessLogicException(ExceptionCode.UNABLE_TO_SEND_EMAIL);
        }
    }

    /**
     * 발신할 이메일 데이터를 설정하는 메서드입니다.
     * SimpleMailMessage 객체에 이메일 수신자, 제목, 내용 등을 설정합니다.
     *
     * @param toEmail 이메일 수신자 주소
     * @param title   이메일 제목
     * @param text    이메일 내용
     * @return 발신할 이메일 데이터를 포함하는 SimpleMailMessage 객체
     */
    private SimpleMailMessage createEmailForm(String toEmail,
                                              String title,
                                              String text) {
        // SimpleMailMessage 객체를 생성합니다.
        SimpleMailMessage message = new SimpleMailMessage();
        // 수신자 이메일 주소를 설정합니다.
        message.setTo(toEmail);
        // 이메일 제목을 설정합니다.
        message.setSubject(title);
        // 이메일 내용을 설정합니다.
        message.setText(text);

        // 설정된 이메일 메시지를 반환합니다.
        return message;
    }
}
