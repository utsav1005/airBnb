package com.bhavsar.airBnb.repository;

import com.bhavsar.airBnb.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

}