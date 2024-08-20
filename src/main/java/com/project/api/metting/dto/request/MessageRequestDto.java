package com.project.api.metting.dto.request;


import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequestDto {
    private String message;
    private String sender;
}
