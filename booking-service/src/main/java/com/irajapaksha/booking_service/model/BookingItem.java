package com.irajapaksha.booking_service.model;

import java.time.Instant;


public class BookingItem {
    public String bookingId;
    public String userId;
    public String hotelId;
    public String roomId;
    public String checkInDate;   // ISO yyyy-MM-dd
    public String checkOutDate;  // ISO
    public String status;        // PENDING | CONFIRMED | CANCELLED
    public Instant createdAt;
}
