package com.irajapaksha.auth_service.controller;

import com.irajapaksha.auth_service.dto.*;
import com.irajapaksha.auth_service.service.AuthService;
import com.online_hotel_booking_system.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponseDto>> signup(
            @RequestBody SignUpRequestDto request) {
        SignUpResponseDto response = authService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User registered successfully", response));

    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<SignInResponseDto>> signin(
            @RequestBody SignInRequestDto request) {
        SignInResponseDto response = authService.signin(request);
        return ResponseEntity
                .ok(new ApiResponse<>(true, "User signed in successfully", response));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> adminEndpoint() {
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Hello, Admin!",
                "This is a protected admin endpoint."));
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> userEndpoint() {
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Hello, User!",
                "This is a protected user endpoint."));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<String>> confirmSignup(@RequestBody ConfirmRequestDto request) {
        authService.confirmSignup(request);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "User confirmed successfully",
                "User confirmation successful."));
    }


}
