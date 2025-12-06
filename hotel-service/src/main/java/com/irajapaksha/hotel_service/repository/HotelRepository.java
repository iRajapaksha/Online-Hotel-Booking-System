package com.irajapaksha.hotel_service.repository;

import com.irajapaksha.hotel_service.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}