package com.irajapaksha.hotel_service.controller;

import com.irajapaksha.hotel_service.Dto.HotelRequestDto;
import com.irajapaksha.hotel_service.Dto.HotelResponseDto;
import com.irajapaksha.hotel_service.Dto.UploadUrl;
import com.irajapaksha.hotel_service.model.Hotel;
import com.irajapaksha.hotel_service.service.HotelService;
import com.online_hotel_booking_system.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService service;

    @PostMapping
    public ResponseEntity<ApiResponse<HotelResponseDto>> addHotel(
            @RequestBody HotelRequestDto req) {
        HotelResponseDto response = service.addHotel(req);
        return ResponseEntity
                .status(201)
                .body(ApiResponse.success("Hotel added successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HotelResponseDto>>> getAll() {
        List<HotelResponseDto> hotels = service.getAll();
        return ResponseEntity.ok(
                ApiResponse.success("Hotels retrieved successfully", hotels)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HotelResponseDto>> getById(
            @PathVariable Long id) {
        HotelResponseDto hotel = service.getById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Hotel retrieved successfully", hotel)
        );
    }

    @PostMapping("/upload-images")
    public ResponseEntity<ApiResponse<List<UploadUrl>>> uploadHotelImages(
            @RequestBody List<String> fileNames) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Presigned URLs generated successfully",
                        service.generateUrls(fileNames)
                )
        );
    }
}