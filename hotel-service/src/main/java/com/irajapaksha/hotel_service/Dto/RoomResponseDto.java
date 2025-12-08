package com.irajapaksha.hotel_service.Dto;

import com.irajapaksha.hotel_service.model.Hotel;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomResponseDto {
    private Long roomId;
    private String roomType;
    private Double price;
    private Integer maxGuests;
    private Boolean isAvailable;
    private Long hotelId;
}
