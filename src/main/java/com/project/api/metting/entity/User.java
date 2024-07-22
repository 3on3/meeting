package com.project.api.metting.entity;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@ToString()
@EqualsAndHashCode(of ="id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mt_users")
public class User {
    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "mt_user_id")
    private String id; //  고유 아이디

    @Column(name = "mt_user_email", nullable = false, unique = true)
    private String email; // 이메일


    @Column(name = "mt_user_password", length = 500)
    private String password; // 패스워드


    @Column(name = "mt_user_name", length = 30)
    private String name; // 이름


    @Column(name = "mt_user_birth_date")
    private Date birthDate; // 생년월일


    @Column(name = "mt_user_phone_number", unique = true)
    private String phoneNumber; // 폰 번호


    @Column(name = "mt_user_univ")
    private String univ; // 대학교


    @Column(name = "mt_user_major")
    private String major; // 전공


    @Enumerated(EnumType.STRING)
    @Column(name = "mt_user_gender")
    private Gender gender; // 성별

    @Column(name = "mt_user_nickname", unique = true)
    private String nickname; // 닉네임


    @Column(name = "mt_user_is_withdrawn")
    @Builder.Default
    private boolean isWithdrawn = false; // 탈퇴여부


    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "mt_user_auth", nullable = false)
    private Auth auth = Auth.COMMON; // 권한



    @Column(name = "mt_user_registered_at")
    @Builder.Default // 가입시간 기본으로 생성
    private LocalDateTime registeredAt = LocalDateTime.now();



    @Column(name = "mt_user_email_is_verificationed")
    @Builder.Default
    private Boolean isVerification = false;


}
