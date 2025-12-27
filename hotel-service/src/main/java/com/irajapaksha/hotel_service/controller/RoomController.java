package com.irajapaksha.hotel_service.controller;


import com.irajapaksha.hotel_service.Dto.RoomRequestDto;
import com.irajapaksha.hotel_service.Dto.RoomResponseDto;
import com.irajapaksha.hotel_service.model.RoomStatus;
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
            @RequestBody RoomStatus status) {
        RoomResponseDto room = service.updateAvailability(id, status);
        return ResponseEntity.ok(
                ApiResponse.success("Room availability updated successfully", room)
        );
    }

    @GetMapping("/rooms/{id}")
    public ResponseEntity<ApiResponse<RoomResponseDto>> getRoomById(
            @PathVariable Long id) {
        RoomResponseDto room = service.getRoomById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Room retrieved successfully", room)
        );
    }

    //get all rooms
    @GetMapping("/hotels/rooms/all")
    public ResponseEntity<ApiResponse<List<RoomResponseDto>>> getAllRooms() {
        List<RoomResponseDto> rooms = service.getAllRooms();
        return ResponseEntity.ok(
                ApiResponse.success("All rooms retrieved successfully", rooms)
        );
    }

    //delete a room
    @DeleteMapping("/hotels/rooms/{id}")
    public ResponseEntity<ApiResponse<String>> deleteRoom(
            @PathVariable Long id) {
        service.deleteRoom(id);
        return ResponseEntity.ok(
                ApiResponse.success("Room deleted successfully", "Room with id " + id + " deleted")
        );
    }

    //update a room
    @PatchMapping("/hotels/rooms/{id}")
    public ResponseEntity<ApiResponse<RoomResponseDto>> updateRoom(
            @PathVariable Long id,
            @RequestBody RoomRequestDto req) {
        RoomResponseDto room = service.updateRoom(id, req);
        return ResponseEntity.ok(
                ApiResponse.success("Room updated successfully", room)
        );
    }



}