package com.irajapaksha.hotel_service.service;


import com.irajapaksha.hotel_service.Dto.HotelRequestDto;
import com.irajapaksha.hotel_service.Dto.HotelResponseDto;
import com.irajapaksha.hotel_service.Dto.RoomResponseDto;
import com.irajapaksha.hotel_service.Dto.UploadUrl;
import com.irajapaksha.hotel_service.model.Hotel;
import com.irajapaksha.hotel_service.model.Room;
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
            return new UploadUrl(fileName, url);
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
            return new RoomResponseDto(
                    room.getId(),
                    room.getRoomType(),
                    room.getPrice(),
                    room.getMaxGuests(),
                    isAvailable,
                    room.getHotelId()
            );
        }).collect(Collectors.toList());
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


}