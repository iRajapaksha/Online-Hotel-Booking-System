//package com.irajapaksha.booking_service.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import software.amazon.awssdk.services.sns.SnsClient;
//import software.amazon.awssdk.services.sns.model.PublishRequest;
//
//@Service
//@RequiredArgsConstructor
//public class BookingEventPublisher {
//    private final SnsClient snsClient;
//
//    @Value("${aws.sns.bookingCreatedTopicArn}")
//    private String topicArn;
//
//    public void publishBookingCreated(BookingCreatedEvent event) {
//
//        PublishRequest request = PublishRequest.builder()
//                .topicArn(topicArn)
//                .message(JsonUtils.toJson(event))
//                .build();
//
//        snsClient.publish(request);
//    }
//}
