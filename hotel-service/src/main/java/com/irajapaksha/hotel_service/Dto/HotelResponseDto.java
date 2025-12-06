package com.irajapaksha.hotel_service.Dto;

import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelResponseDto {
    private Long hotelId;
    String name;
    String location;
    String description;
    Double rating;
    List<String> images;
}
