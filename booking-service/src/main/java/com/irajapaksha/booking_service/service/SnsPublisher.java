package com.irajapaksha.booking_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.online_hotel_booking_system.event.BookingCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.Map;

@Service
public class SnsPublisher {
    private final SnsClient snsClient;
    private final String topicArn;
    private final ObjectMapper mapper = new ObjectMapper();

    public SnsPublisher(SnsClient snsClient, @Value("${aws.sns.topic-arn}") String topicArn) {
        this.snsClient = snsClient;
        this.topicArn = topicArn;
    }

    public void publishBookingCreated(Object payload) {
        try {
            System.out.println("Publishing to SNS topic: " + topicArn);
            String json = mapper.writeValueAsString(payload);
            PublishRequest req = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(json)
                    .messageAttributes(Map.of("eventType",
                            software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                                    .dataType("String").stringValue("booking_created").build()))
                    .build();
            snsClient.publish(req);
        } catch (Exception e) {
            // log but don't fail the booking (decouple)
            e.printStackTrace();
        }
    }
}
