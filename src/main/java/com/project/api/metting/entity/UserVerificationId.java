package com.project.api.metting.entity;

import java.io.Serializable;
import java.util.Objects;

public class UserVerificationId implements Serializable {

    private String id;  // UserVerification 엔티티의 id
    private String email;  // UserVerification 엔티티의 email

    // 기본 생성자
    public UserVerificationId() {

    }

    // 매개변수 생성자
    public UserVerificationId(String id, String email) {
        this.id = id;
        this.email = email;
    }

    // equals 및 hashCode 메서드 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserVerificationId that = (UserVerificationId) o;
        return Objects.equals(id, that.id) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}