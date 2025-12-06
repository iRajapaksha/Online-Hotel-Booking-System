package com.irajapaksha.booking_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookingResponseDto {
    public String bookingId;
    public String status;
}
