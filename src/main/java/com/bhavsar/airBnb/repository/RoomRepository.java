package com.bhavsar.airBnb.repository;

import com.bhavsar.airBnb.model.Hotel;
import com.bhavsar.airBnb.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByHotel(Hotel hotel);


}