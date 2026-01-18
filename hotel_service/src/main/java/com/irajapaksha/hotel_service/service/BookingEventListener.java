package com.irajapaksha.hotel_service.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.irajapaksha.hotel_service.model.Room;
import com.irajapaksha.hotel_service.repository.RoomRepository;
import com.online_hotel_booking_system.event.BookingCreatedEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingEventListener {

    private final RoomRepository roomRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @SqsListener("hotel-booking-events-queue")
    public void handleBookingCreated(String message) throws Exception {

        System.out.println("Raw SQS message: " + message);

        BookingCreatedEvent bookingEvent =
                objectMapper.readValue(message, BookingCreatedEvent.class);

        System.out.println(
                "Booking ID: " + bookingEvent.getBookingId() +
                        ", Room ID: " + bookingEvent.getRoomId()
        );

        Room room = roomRepository.findById(Long.parseLong(bookingEvent.getRoomId())).orElseThrow();
        room.getBookingIds().add(bookingEvent.getBookingId());
        roomRepository.save(room);


    }
}
