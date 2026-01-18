package com.irajapaksha.hotel_service.dto;

import com.irajapaksha.hotel_service.model.RoomStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomResponseDto {

    private Long roomId;
    private String roomName;
    private String hotelName;
    private String roomType;
    private Double pricePerNight;
    private Integer capacity;
    private RoomStatus status;
    private Long hotelId;
    private List<String> bookingIds;
}
