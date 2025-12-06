package com.irajapaksha.hotel_service.service;


import com.irajapaksha.hotel_service.Dto.HotelRequestDto;
import com.irajapaksha.hotel_service.Dto.HotelResponseDto;
import com.irajapaksha.hotel_service.Dto.UploadUrl;
import com.irajapaksha.hotel_service.model.Hotel;
import com.irajapaksha.hotel_service.repository.HotelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HotelService {

    private final HotelRepository repo;
    private final S3Service s3Service;

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