package com.bhavsar.airBnb.repository;

import com.bhavsar.airBnb.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
