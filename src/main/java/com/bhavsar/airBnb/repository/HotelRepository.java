package com.bhavsar.airBnb.repository;

import com.bhavsar.airBnb.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface HotelRepository extends JpaRepository<Hotel, Long> {

}