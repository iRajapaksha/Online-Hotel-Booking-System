package com.irajapaksha.booking_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient webClient(@Value("${hotel.service.baseUrl}") String baseUrl) {
        return WebClient.builder()
                .filter((request, next) -> {
                    var context = ReactiveSecurityContextHolder.getContext();
                    return context
                            .map(securityContext -> securityContext.getAuthentication())
                            .flatMap(auth -> {
                                var token = (Jwt) auth.getPrincipal();
                                return next.exchange(
                                        ClientRequest.from(request)
                                                .header("Authorization", "Bearer " + token.getTokenValue())
                                                .build()
                                );
                            });
                })
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public WebClient hotelWebClient(@Value("${hotel.service.baseUrl}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

}
