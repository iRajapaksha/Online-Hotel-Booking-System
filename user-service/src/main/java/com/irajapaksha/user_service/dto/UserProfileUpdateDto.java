package com.irajapaksha.user_service.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileUpdateDto {
    String email;
    String fullName;
    String phone;
    String address;
    Map<String,String> preferences;
}
