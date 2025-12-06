package com.irajapaksha.booking_service.controller;

import com.irajapaksha.booking_service.dto.CreateBookingRequestDto;
import com.irajapaksha.booking_service.dto.CreateBookingResponseDto;
import com.irajapaksha.booking_service.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService svc;

    public BookingController(BookingService svc) { this.svc = svc; }

    @PostMapping
    public ResponseEntity<CreateBookingResponseDto> create(@Valid @RequestBody CreateBookingRequestDto req) {
        try {
            String bookingId = svc.createBooking(req);
            return ResponseEntity.ok(new CreateBookingResponseDto(bookingId, "CONFIRMED"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(new CreateBookingResponseDto(null, "CONFLICT: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new CreateBookingResponseDto(null, e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new CreateBookingResponseDto(null, "ERROR"));
        }
    }
}