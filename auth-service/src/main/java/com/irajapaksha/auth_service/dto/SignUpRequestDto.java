package com.irajapaksha.auth_service.dto;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequestDto {
    private String password;
    private String email;
    private String fullName;
    private String role;
}
