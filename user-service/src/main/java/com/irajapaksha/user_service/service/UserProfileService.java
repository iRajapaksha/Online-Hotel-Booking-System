package com.irajapaksha.user_service.service;

import com.irajapaksha.user_service.dto.UserProfileDto;
import com.irajapaksha.user_service.model.UserProfile;
import com.irajapaksha.user_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository repo;


    public UserProfileDto findByEmail(String email) {
        UserProfile user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User profile not found for email: " + email));
        return mapToDto(user);
    }

    public UserProfileDto createOrUpdate(String userId, String email, String fullName, String phone,
                                      String address, Map<String, String> preferences) {
        UserProfile p = repo.findById(userId).orElse(new UserProfile());
        p.setUserId(userId);
        if (email != null) p.setEmail(email);
        if (fullName != null) p.setFullName(fullName);
        if (phone != null) p.setPhone(phone);
        if (address != null) p.setAddress(address);
        if (preferences != null) p.setPreferences(preferences);
        repo.save(p);
        return mapToDto(p);
    }

    public UserProfileDto patchUpdate(String userId, Map<String, Object> updates) {
        UserProfile existing = getById(userId);
        if (updates.containsKey("fullName")) existing.setFullName((String) updates.get("fullName"));
        if (updates.containsKey("phone")) existing.setPhone((String) updates.get("phone"));
        if (updates.containsKey("address")) existing.setAddress((String) updates.get("address"));
        if (updates.containsKey("preferences")) {
            // careful: cast
            Object prefsObj = updates.get("preferences");
            if (prefsObj instanceof Map<?, ?>) {
                // convert keys/values to string map
                Map<String, String> pref = (Map) prefsObj;
                existing.setPreferences(pref);
            }
        }
        repo.update(existing);
        return mapToDto(existing);
    }

    public UserProfileDto mapToDto(UserProfile profile) {
        return UserProfileDto.builder()
                .userId(profile.getUserId())
                .email(profile.getEmail())
                .fullName(profile.getFullName())
                .phone(profile.getPhone())
                .address(profile.getAddress())
                .preferences(profile.getPreferences())
                .roles(profile.getRoles())
                .build();
    }
    public UserProfile getById(String userId) {
        return repo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found for id: " + userId));
    }
}

