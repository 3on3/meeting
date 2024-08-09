package com.project.api.metting.dto.response;

import com.project.api.metting.entity.User;
import lombok.*;

import java.util.Objects;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class MainWebSocketResponseDto {
    private String type;
    private LoginResponseDto loginUser;
    private User user;
}
