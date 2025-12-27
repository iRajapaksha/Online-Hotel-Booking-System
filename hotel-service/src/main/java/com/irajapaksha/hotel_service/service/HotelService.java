package com.irajapaksha.hotel_service.service;


import com.irajapaksha.hotel_service.Dto.HotelRequestDto;
import com.irajapaksha.hotel_service.Dto.HotelResponseDto;
import com.irajapaksha.hotel_service.Dto.RoomResponseDto;
import com.irajapaksha.hotel_service.Dto.UploadUrl;
import com.irajapaksha.hotel_service.model.Hotel;
import com.irajapaksha.hotel_service.model.Room;
import com.irajapaksha.hotel_service.model.RoomStatus;
import com.irajapaksha.hotel_service.repository.HotelRepository;
import com.irajapaksha.hotel_service.repository.RoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HotelService {

    private final HotelRepository repo;
    private final S3Service s3Service;
    private final RestTemplate restTemplate;
    private final RoomRepository roomRepository;
    private final String bookingServiceUrl = "http://booking-service/bookings";

    public HotelResponseDto addHotel(HotelRequestDto req) {
        Hotel hotel = Hotel.builder()
                .name(req.getName())
                .location(req.getLocation())
                .description(req.getDescription())
                .rating(req.getRating())
                .images(req.getImages())
                .build();
        Hotel savedHotel = repo.save(hotel);
        return mapToDto(savedHotel);

    }

    public List<HotelResponseDto> getAll() {
        List<Hotel> hotels = repo.findAll();
        return hotels.stream().map(this::mapToDto).toList();
    }

    public HotelResponseDto getById(Long id) {
        Hotel hotel = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));
        return mapToDto(hotel);

    }

    public List<UploadUrl> generateUrls(List<String> fileNames){
        return fileNames.stream().map(fileName -> {
            fileName = System.currentTimeMillis() + "_" + fileName;
            String url = s3Service.generatePresignedPutUrl(fileName, 3000);
            String publicUrl = s3Service.buildPublicUrl(fileName);
            return new UploadUrl(fileName, url, publicUrl);
        }).toList();

    }
    public Boolean isRoomAvailable(Long roomId, String date) {
        String url = bookingServiceUrl + "/availability?roomId=" + roomId + "&date=" + date;

        Map response = restTemplate.getForObject(url, Map.class);
        return (Boolean) response.get("isAvailable");
    }
    public List<RoomResponseDto> getRoomsWithAvailability(String date) {
        List<Room> rooms = roomRepository.findAll();

        return rooms.stream().map(room -> {
            boolean isAvailable = isRoomAvailable(room.getId(), date);
            if (isAvailable) {
                room.setStatus(RoomStatus.AVAILABLE);
            } else {
                room.setStatus(RoomStatus.OCCUPIED);
            }
            return mapToDto(room);

        }).collect(Collectors.toList());
    }

    public RoomResponseDto mapToDto(Room room) {
        return RoomResponseDto.builder()
                .roomId(room.getId())
                .roomType(room.getRoomType())
                .roomName(room.getRoomName())
                .pricePerNight(room.getPricePerNight())
                .capacity(room.getCapacity())
                .hotelId(room.getHotelId())
                .status(room.getStatus())
                .build();
    }



    public HotelResponseDto mapToDto(Hotel hotel) {
        return HotelResponseDto.builder()
                .hotelId(hotel.getId())
                .name(hotel.getName())
                .location(hotel.getLocation())
                .description(hotel.getDescription())
                .rating(hotel.getRating())
                .images(hotel.getImages())
                .build();
    }


    public HotelResponseDto updateHotel(HotelRequestDto req, Long id) {
        Hotel existingHotel = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));

        if (req.getName() != null) existingHotel.setName(req.getName());
        if (req.getLocation() != null) existingHotel.setLocation(req.getLocation());
        if (req.getDescription() != null) existingHotel.setDescription(req.getDescription());
        if (req.getRating() != null) existingHotel.setRating(req.getRating());
        if (req.getImages() != null) existingHotel.setImages(req.getImages());

        Hotel updatedHotel = repo.save(existingHotel);
        return mapToDto(updatedHotel);
    }

    public void deleteHotel(Long id) {
        repo.deleteById(id);
    }
}