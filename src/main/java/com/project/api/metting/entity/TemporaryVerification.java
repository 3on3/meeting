package com.project.api.metting.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;

@Getter
@Setter
@ToString()
@EqualsAndHashCode(of ="id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "temporary_verification")
public class TemporaryVerification {

    // - 데이터베이스에서 임시 인증과 관련된 정보를 저장하고 관리

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "temporary_verification_id")
    private String id; // 고유 아이디 (UUID로 자동 생성)

    //, unique = true
    @Column(name = "mt_user_temporary_verification_email", nullable = false)
    private String email; // 이메일 (필수, 유일)

    @Column(name = "mt_user_temporary_verification_code", nullable = false)
    private String code; // 인증 코드 (필수)










}