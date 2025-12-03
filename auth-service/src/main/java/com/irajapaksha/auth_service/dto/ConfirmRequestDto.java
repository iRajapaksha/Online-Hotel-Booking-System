package com.irajapaksha.auth_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmRequestDto {
    private String email;
    private String code;
}
