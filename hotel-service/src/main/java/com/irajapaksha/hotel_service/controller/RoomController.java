package com.irajapaksha.hotel_service.controller;


import com.irajapaksha.hotel_service.Dto.RoomRequestDto;
import com.irajapaksha.hotel_service.Dto.RoomResponseDto;
import com.irajapaksha.hotel_service.service.RoomService;
import com.online_hotel_booking_system.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService service;


    @PostMapping("/hotels/{id}/rooms")
    public ResponseEntity<ApiResponse<RoomResponseDto>> addRoom(
            @PathVariable Long id, @RequestBody RoomRequestDto req) {
        RoomResponseDto response = service.addRoom(id, req);
        return ResponseEntity
                .status(201)
                .body(ApiResponse.success("Room added successfully", response));
    }

    @GetMapping("/hotels/{id}/rooms")
    public ResponseEntity<ApiResponse<List<RoomResponseDto>>> getRooms(
            @PathVariable Long id) {
        List<RoomResponseDto> rooms = service.getRoomsByHotelId(id);
        return ResponseEntity.ok(
                ApiResponse.success("Rooms retrieved successfully", rooms)
        );
    }

    @PutMapping("/rooms/{id}/availability")
    public ResponseEntity<ApiResponse<RoomResponseDto>> updateAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {
        RoomResponseDto room = service.updateAvailability(id, available);
        return ResponseEntity.ok(
                ApiResponse.success("Room availability updated successfully", room)
        );
    }
}