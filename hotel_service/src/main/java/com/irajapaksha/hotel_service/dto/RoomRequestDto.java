package com.irajapaksha.hotel_service.dto;

import com.irajapaksha.hotel_service.model.RoomStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomRequestDto {
    private String roomName;
    private String hotelName;
    private String roomType;
    private Double pricePerNight;
    private Integer capacity;
    private RoomStatus status;

}
