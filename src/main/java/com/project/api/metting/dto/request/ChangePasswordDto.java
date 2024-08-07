package com.project.api.metting.dto.request;

import lombok.Data;

@Data
public class ChangePasswordDto {

    private String currentPassword; //현재 비밀번호
    private String newPassword; //새로운 비밀번호
    private String confirmNewPassword; //새로운 비밀번호 확인

}
