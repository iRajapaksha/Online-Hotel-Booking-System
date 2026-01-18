package com.irajapaksha.hotel_service.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@Getter
@Setter
public class AwsSecretConfig {

    private String dbUser;
    private String dbPassword;
    private String dbUrl;
    private String s3Bucket;
    private String cognitoJwkUrl;

    @PostConstruct
    public void init() {
        try {
            SecretsManagerClient client = SecretsManagerClient.builder()
                    .region(Region.AP_SOUTH_1)
                    .build();

            String secretName = "hotel-service-secrets";
            String secretString = client.getSecretValue(GetSecretValueRequest.builder()
                            .secretId(secretName)
                            .build())
                    .secretString();

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> secrets = mapper.readValue(secretString, new TypeReference<Map<String,String>>() {});


            this.dbUser = secrets.get("DB_USER");
            this.dbPassword = secrets.get("DB_PASSWORD");
            this.dbUrl = secrets.get("DB_URL");
            this.s3Bucket = secrets.get("S3_BUCKET");
            this.cognitoJwkUrl = secrets.get("COGNITO_JWK_URL");


            if (dbUrl != null) System.setProperty("spring.datasource.url", dbUrl);
            if (dbUser != null) System.setProperty("spring.datasource.username", "postgres");
            if (dbPassword != null) System.setProperty("spring.datasource.password", dbPassword);
            if (s3Bucket != null) System.setProperty("aws.s3.bucket", s3Bucket);
            if (cognitoJwkUrl != null) {
                System.setProperty("aws.cognito.jwkUrl", cognitoJwkUrl);
                System.setProperty("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",cognitoJwkUrl);
            };


        } catch (Exception e) {
            throw new RuntimeException("Failed to load AWS secrets", e);
        }
    }

    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(dbUrl);
        ds.setUsername("postgres");
        ds.setPassword(dbPassword);
        return ds;
    }
}