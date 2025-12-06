package com.irajapaksha.hotel_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomType;
    private Double price;
    private Integer maxGuests;
    private Boolean isAvailable = true;
    private Long hotelId;

}