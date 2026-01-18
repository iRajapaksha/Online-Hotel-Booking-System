package com.irajapaksha.hotel_service.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelRequestDto {
    String name;
    String location;
    String description;
    Double rating;
    List<String> images;
}
