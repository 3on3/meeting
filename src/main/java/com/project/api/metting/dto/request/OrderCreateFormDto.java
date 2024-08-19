package com.project.api.metting.dto.request;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateFormDto {

    private String name;
    private int totalPrice;
}
