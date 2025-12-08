package com.irajapaksha.booking_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityResponseDto {
    private String roomId;
    private boolean isAvailable;
    private String date;
}
