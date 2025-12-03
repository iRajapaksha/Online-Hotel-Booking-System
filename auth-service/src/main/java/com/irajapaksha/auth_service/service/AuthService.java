package com.irajapaksha.auth_service.service;

import com.irajapaksha.auth_service.dto.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {
    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    @Value("${aws.cognito.appClientId}")
    private String clientId;

    @Value("${aws.cognito.clientSecret}")
    private String clientSecret;

    @Value("${aws.cognito.region}")
    private String region;

    private CognitoIdentityProviderClient cognitoClient;

    @PostConstruct
    public void init() {
        cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.of(region))
                .build();
    }

    public SignUpResponseDto signup(SignUpRequestDto request) {
        String secretHash = calculateSecretHash(clientId, clientSecret, request.getEmail());

        List<AttributeType> attributes = new ArrayList<>();
        attributes.add(AttributeType.builder().name("email").value(request.getEmail()).build());
        attributes.add(AttributeType.builder().name("name").value(request.getFullName()).build());



        SignUpRequest signUp = SignUpRequest.builder()
                .clientId(clientId)
                .secretHash(secretHash)
                .username(request.getEmail())
                .password(request.getPassword())
                .userAttributes(attributes)
                .build();

        cognitoClient.signUp(signUp);

        // Assign role/group
        AdminAddUserToGroupRequest addToGroup = AdminAddUserToGroupRequest.builder()
                .userPoolId(userPoolId)
                .username(request.getEmail())
                .groupName(request.getRole().toUpperCase())
                .build();

        cognitoClient.adminAddUserToGroup(addToGroup);

        return new SignUpResponseDto(
                request.getEmail(),
                request.getRole()
        );
    }

    public SignInResponseDto signin(SignInRequestDto request) {
        String secretHash = calculateSecretHash(clientId, clientSecret, request.getEmail());

        AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                .userPoolId(userPoolId)
                .clientId(clientId)
                .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                .authParameters(Map.of(
                        "USERNAME", request.getEmail(),
                        "PASSWORD", request.getPassword(),
                        "SECRET_HASH", secretHash
                ))
                .build();

        AdminInitiateAuthResponse response = cognitoClient.adminInitiateAuth(authRequest);

        return new SignInResponseDto(
                response.authenticationResult().idToken(),
                response.authenticationResult().accessToken(),
                response.authenticationResult().refreshToken()
        );
    }
    public String confirmSignup(ConfirmRequestDto dto) {
        String secretHash = calculateSecretHash(clientId, clientSecret, dto.getEmail());

        ConfirmSignUpRequest request = ConfirmSignUpRequest.builder()
                .clientId(clientId)
                .username(dto.getEmail())
                .confirmationCode(dto.getCode())
                .secretHash(secretHash)
                .build();

        cognitoClient.confirmSignUp(request);

        return "Account confirmed successfully!";
    }

    public String calculateSecretHash(String userPoolClientId, String clientSecret, String userName) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(clientSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(key);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating SECRET_HASH", e);
        }
    }

}
