package com.irajapaksha.hotel_service.service;


import com.irajapaksha.hotel_service.dto.RoomRequestDto;
import com.irajapaksha.hotel_service.dto.RoomResponseDto;
import com.irajapaksha.hotel_service.model.Room;
import com.irajapaksha.hotel_service.model.RoomStatus;
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
                .roomName(req.getRoomName())
                .hotelName(req.getHotelName())
                .pricePerNight(req.getPricePerNight())
                .capacity(req.getCapacity())
                .status(req.getStatus())
                .hotelId(hotelId)
                .build();
        Room savedRoom = repo.save(room);
        return mapToDto(savedRoom);
    }

    public List<RoomResponseDto> getRooms(Long hotelId) {
        List<Room> rooms = repo.findByHotelId(hotelId);
        return rooms.stream().map(this::mapToDto).toList();
    }

    public RoomResponseDto updateAvailability(Long roomId, RoomStatus status) {
        Room room = repo.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
        room.setStatus(status);
        Room updatedRoom = repo.save(room);
        return mapToDto(updatedRoom);
    }

    public RoomResponseDto mapToDto(Room room) {
        return RoomResponseDto.builder()
                .roomId(room.getId())
                .roomName(room.getRoomName())
                .hotelName(room.getHotelName())
                .roomType(room.getRoomType())
                .pricePerNight(room.getPricePerNight())
                .capacity(room.getCapacity())
                .status(room.getStatus())
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

    public List<RoomResponseDto> getAllRooms() {
        List<Room> rooms = repo.findAll();
        return rooms.stream().map(this::mapToDto).toList();
    }

    public void deleteRoom(Long id) {
        repo.deleteById(id);
    }

    public RoomResponseDto updateRoom(Long id, RoomRequestDto req) {
        Room room = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
        //check if fields null and set new values
        if (req.getRoomName() != null) room.setRoomName(req.getRoomName());
        if (req.getHotelName() != null) room.setHotelName(req.getHotelName());
        if (req.getRoomType() != null) room.setRoomType(req.getRoomType());
        if (req.getPricePerNight() != null) room.setPricePerNight(req.getPricePerNight());
        if (req.getCapacity() != null) room.setCapacity(req.getCapacity());
        if (req.getStatus() != null) room.setStatus(req.getStatus());

        Room updatedRoom = repo.save(room);
        return mapToDto(updatedRoom);
    }

    public List<RoomResponseDto> searchRooms(String location, Integer capacity, String checkInDate, String checkOutDate) {
        List<Long> hotelIds = hotelService.getHotelIdsByLocation(location);
        List<Room> rooms = repo.findByHotelIdInAndCapacityGreaterThanEqual(hotelIds, capacity);
        return rooms.stream().map(this::mapToDto).toList();
    }
}