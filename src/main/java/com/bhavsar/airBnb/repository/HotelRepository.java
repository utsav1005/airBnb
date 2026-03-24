package com.bhavsar.airBnb.repository;

import com.bhavsar.airBnb.model.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HotelRepository extends JpaRepository<Hotel, Long> {
    Page<Hotel> findAll(Pageable pageable);
}