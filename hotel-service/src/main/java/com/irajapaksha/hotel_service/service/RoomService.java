package com.irajapaksha.hotel_service.service;

import com.irajapaksha.hotel_service.Dto.RoomRequestDto;
import com.irajapaksha.hotel_service.Dto.RoomResponseDto;
import com.irajapaksha.hotel_service.model.Room;
import com.irajapaksha.hotel_service.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository repo;
    private final HotelService hotelService;

    public RoomResponseDto addRoom(Long hotelId, RoomRequestDto req) {
        Room room = Room.builder()
                .roomType(req.getRoomType())
                .price(req.getPrice())
                .maxGuests(req.getMaxGuests())
                .isAvailable(true)
                .hotelId(hotelId)
                .build();
        Room savedRoom = repo.save(room);
        return mapToDto(savedRoom);
    }

    public List<RoomResponseDto> getRooms(Long hotelId) {
        List<Room> rooms = repo.findByHotelId(hotelId);
        return rooms.stream().map(this::mapToDto).toList();
    }

    public RoomResponseDto updateAvailability(Long roomId, boolean available) {
        Room room = repo.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
        room.setIsAvailable(available);
        Room updatedRoom = repo.save(room);
        return mapToDto(updatedRoom);
    }

    public RoomResponseDto mapToDto(Room room) {
        return RoomResponseDto.builder()
                .roomId(room.getId())
                .roomType(room.getRoomType())
                .price(room.getPrice())
                .maxGuests(room.getMaxGuests())
                .isAvailable(room.getIsAvailable())
                .hotelId(room.getHotelId())
                .build();
    }

    public List<RoomResponseDto> getRoomsByHotelId(Long id) {
        List<Room> rooms = repo.findByHotelId(id);
        return rooms.stream().map(this::mapToDto).toList();
    }

    public RoomResponseDto getRoomById(Long id) {
        Room room = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
        return mapToDto(room);
    }
}