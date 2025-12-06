package com.irajapaksha.hotel_service.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomRequestDto {
    private String roomType;
    private Double price;
    private Integer maxGuests;

}
