package com.irajapaksha.booking_service.controller;

import com.irajapaksha.booking_service.dto.AvailabilityResponseDto;
import com.irajapaksha.booking_service.dto.CreateBookingRequestDto;
import com.irajapaksha.booking_service.dto.CreateBookingResponseDto;
import com.irajapaksha.booking_service.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @GetMapping("/availability")
    public AvailabilityResponseDto checkAvailability(
            @RequestParam String roomId,
            @RequestParam String date
    ) {
        return svc.checkAvailability(roomId, date);
    }

}