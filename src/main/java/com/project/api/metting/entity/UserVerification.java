package com.project.api.metting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;


import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mt_user_verifications")
@IdClass(UserVerificationId.class)  // 복합 키 클래스를 설정
public class UserVerification implements Serializable {

    @Id
    @Column(name = "mt_user_verification_id")
    private String id;

     @Id
    @Column(name = "email")
    private String email;

    @Column(name = "mt_user_verification_code", nullable = false)
    private String verificationCode; // 인증 코드

    @Column(name = "mt_user_verification_expiry_date", nullable = false)
    private LocalDateTime expiryDate; // 인증 만료시간

    @ManyToOne
    @JoinColumn(name = "verification_user_id", referencedColumnName = "mt_user_id", nullable = false)
    private User user;
}


///**
// * UserVerification
// * : 유저 이메일 인증 코드 엔터티
// * user (FK)
// * : 유저 아이디 (1 : 1)
// */
//
//@Getter
//@ToString
//@EqualsAndHashCode(of = "id")
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//
//@Entity
//@Table(name = "mt_user_verifications")
//public class UserVerification {
//
//    @Id
//    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
//    @GeneratedValue(generator = "uuid-generator")
//    @Column(name = "mt_user_verification_id")
//    private String id; //고유 아이디
//
//    @Column(name = "mt_user_verification_code", nullable = false)
//    private String verificationCode; // 인증 코드
//
//    @Column(name = "mt_user_verification_expiry_date",nullable = false)
//    private LocalDateTime expiryDate; // 인증 만료시간
//
//
//    @JsonIgnore
//    @ToString.Exclude
//    @OneToOne
//    @JoinColumn(name = "verification_user_id", referencedColumnName = "mt_user_id", nullable = false, unique = true)
//    private User user;
//
//    @Id
//    @Setter
//    private String email;
//    @Setter
//    private String code;
//    @Setter
//    private LocalDateTime expirationTime;
//
//}
