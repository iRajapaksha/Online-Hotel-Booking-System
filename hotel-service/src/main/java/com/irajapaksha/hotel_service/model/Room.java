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
    private String roomName;
    private String hotelName;
    private String roomType;
    private Double pricePerNight;
    private Integer capacity;
    private RoomStatus status;
    private Long hotelId;

}