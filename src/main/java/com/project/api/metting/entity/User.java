package com.project.api.metting.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.aspectj.apache.bcel.classfile.Code;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * User
 * : 유저 정보를 담은 유저 엔터티
 * userMemberShip, userVerification, userProfile (excludes)
 * : 해당 유저 멤버십, 이메일 인증 정보, 프로필 정보 (1 : 1)
 * groupUser, chatMessages (excludes)
 * : 해당 유저의 그룹 정보, 메세지 정보 (1 : M - GroupUser, ChatMessages)
 */
@Getter
@Setter
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


    @Column(name = "mt_user_univ_name")
    private String univName; // 대학교


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
    @Setter
    private Boolean isVerification = false;

    @Setter
    @Column(name = "mt_user_refresh_token", nullable = true, unique = true)
    private String refreshToken; // 리프레쉬 토큰

    @Setter
    @Column(name = "mt_user_refresh_token_expiry_date", nullable = true)
    private Instant refreshTokenExpiryDate; // 리프레쉬 토큰 유통기한

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupUser> groupUsers;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> chatMessages;

    @JsonIgnore
    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile userProfile; //유저프로필

    @Enumerated(EnumType.STRING)
    @Column(name = "mt_user_membership")
    private Membership membership; // 멤버십

    public void confirm(String password, String name, Date Birth, String phone, String univName, String major, Gender gender, String nickname) {
        this.password = password;
        this.name = name;
        this.birthDate = Birth;
        this.phoneNumber = phone;
        this.univName = univName;
        this.major = major;
        this.gender = gender;
        this.nickname = nickname;
        this.registeredAt = LocalDateTime.now();
    }

    public void changePass(String password) {
        this.password = password;
    }

    // 리프레시 토큰과 만료 시간을 업데이트하는 메서드
    public void updateRefreshToken(String refreshToken, Instant expiryDate) {
        this.refreshToken = refreshToken;
        this.refreshTokenExpiryDate = expiryDate;
    }

    public void setIsWithdrawn(boolean b) {
    }
}
