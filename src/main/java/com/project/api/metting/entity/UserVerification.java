package com.project.api.metting.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * UserVerification
 * : 유저 이메일 인증 코드 엔터티
 * user (FK)
 * : 유저 아이디 (1 : 1)
 */

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "mt_user_verifications")
public class UserVerification {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "mt_user_verification_id")
    private String id; //고유 아이디

    @Column(name = "mt_user_verification_code", nullable = false)
    private String verificationCode; // 인증 코드

    @Column(name = "mt_user_verification_expiry_date",nullable = false)
    private LocalDateTime expiryDate; // 인증 만료시간

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mt_user_id",  nullable = false)
    private User user;
}
