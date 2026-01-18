package com.irajapaksha.auth_service.config;


 import com.fasterxml.jackson.core.type.TypeReference;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import jakarta.annotation.PostConstruct;
 import lombok.Getter;
 import lombok.Setter;
 import org.springframework.context.annotation.Configuration;
 import software.amazon.awssdk.regions.Region;
 import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
 import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;


 import java.util.Map;

@Configuration
@Getter
@Setter
public class AwsSecretConfig {

    private String userPoolId;
    private String appClientId;
    private String clientSecret;
    private String cognitoJwkUrl;

    @PostConstruct
    public void init() {
        try {
            SecretsManagerClient client = SecretsManagerClient.builder()
                    .region(Region.AP_SOUTH_1)
                    .build();

            String secretName = "booking-service-secrets";
            String secretString = client.getSecretValue(GetSecretValueRequest.builder()
                            .secretId(secretName)
                            .build())
                    .secretString();

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> secrets = mapper.readValue(secretString, new TypeReference<Map<String,String>>() {});



            this.userPoolId = secrets.get("USER_POOL_ID");
            this.appClientId = secrets.get("APP_CLIENT_ID");
            this.clientSecret = secrets.get("CLIENT_SECRET");
            this.cognitoJwkUrl = secrets.get("COGNITO_JWK_URL");


            if (appClientId != null) System.setProperty("aws.cognito.userPoolId", appClientId);
            if (userPoolId != null) System.setProperty("aws.cognito.appClientId", userPoolId);
            if (clientSecret != null) System.setProperty("aws.cognito.clientSecret", clientSecret);
            if (cognitoJwkUrl != null) {
                System.setProperty("aws.cognito.jwkUrl", cognitoJwkUrl);
                System.setProperty("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",cognitoJwkUrl);
            };


        } catch (Exception e) {
            throw new RuntimeException("Failed to load AWS secrets", e);
        }
    }

}