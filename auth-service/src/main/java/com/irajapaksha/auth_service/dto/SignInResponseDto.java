package com.irajapaksha.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SignInResponseDto {
    private String idToken;
    private String accessToken;
    private String refreshToken;
}
