package com.bhavsar.airBnb.repository;

import com.bhavsar.airBnb.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}