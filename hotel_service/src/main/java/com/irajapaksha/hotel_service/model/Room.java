package com.irajapaksha.hotel_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "room_booking_ids",
            joinColumns = @JoinColumn(name = "room_id")
    )
    @Column(name = "booking_ids")
    @OrderColumn(name = "idx")
    private List<String> bookingIds = new ArrayList<>();

}