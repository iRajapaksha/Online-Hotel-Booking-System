package com.irajapaksha.user_service.controller;


import com.irajapaksha.user_service.dto.UserProfileDto;
import com.irajapaksha.user_service.dto.UserProfileUpdateDto;
import com.irajapaksha.user_service.model.UserProfile;
import com.irajapaksha.user_service.service.UserProfileService;
import com.online_hotel_booking_system.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserProfileController {

        private final UserProfileService service;

        // GET /users/me
        @GetMapping("/me")
        public ResponseEntity<ApiResponse<UserProfileDto>> getMe(@AuthenticationPrincipal Jwt jwt) {
            String sub = jwt.getSubject(); // Cognito sub
            UserProfile p = service.getById(sub);
            UserProfileDto dto = new UserProfileDto(p.getUserId(), p.getEmail(), p.getFullName(),
                    p.getPhone(), p.getAddress(), p.getPreferences(),p.getRoles());
            return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", dto));
        }

        // PUT /users/me  (full update / create)
        @PutMapping("/me")
        public ResponseEntity<ApiResponse<UserProfileDto>> putMe(@AuthenticationPrincipal Jwt jwt,
                                                    @RequestBody UserProfileUpdateDto update) {
            String sub = jwt.getSubject();
            String email = jwt.getClaimAsString("email"); // optionally copy from token
            UserProfileDto p = service.createOrUpdate(sub, email, update.getFullName(), update.getPhone(), update.getAddress(), update.getPreferences());

            return ResponseEntity.ok(ApiResponse.success("User profile created/updated successfully", p));
        }

        // GET /users/{id}
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('ROLE_ADMIN') or #id == authentication.principal.claims['sub']")
        public ResponseEntity<ApiResponse<UserProfileDto>> getById(@PathVariable("id") String id) {
            UserProfile p = service.getById(id);
            UserProfileDto dto = new UserProfileDto(p.getUserId(), p.getEmail(), p.getFullName(),
                    p.getPhone(), p.getAddress(), p.getPreferences(),p.getRoles());
            return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", dto));
        }
    }

