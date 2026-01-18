package com.irajapaksha.booking_service.config;

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

    private String snsTopicArn;
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


            this.snsTopicArn = secrets.get("SNS_TOPIC_ARN");
            this.cognitoJwkUrl = secrets.get("COGNITO_JWK_URL");

            if (snsTopicArn != null) System.setProperty("aws.sns.topic-arn", snsTopicArn);
            if (snsTopicArn != null) System.setProperty("spring.datasource.username", "postgres");
            if (cognitoJwkUrl != null) {
                System.setProperty("aws.cognito.jwkUrl", cognitoJwkUrl);
                System.setProperty("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",cognitoJwkUrl);
            };


        } catch (Exception e) {
            throw new RuntimeException("Failed to load AWS secrets", e);
        }
    }


}