package com.online_hotel_booking_system.event;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCreatedEvent {
    private String bookingId;
    private String roomId;
    private String hotelId;
    private String userId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDate createdAt;
}
