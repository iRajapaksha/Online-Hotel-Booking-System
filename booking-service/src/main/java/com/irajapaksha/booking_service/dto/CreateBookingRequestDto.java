package com.irajapaksha.booking_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookingRequestDto {
    @NotNull
    public String userId;       // Cognito sub
    @NotNull public String hotelId;
    @NotNull public String roomId;
    @NotNull public LocalDate checkInDate;
    @NotNull public LocalDate checkOutDate;
    // optional idempotency key (client provided)
    public String idempotencyKey;
}
