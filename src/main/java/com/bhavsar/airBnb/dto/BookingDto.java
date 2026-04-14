package com.bhavsar.airBnb.dto;

import com.bhavsar.airBnb.model.Guest;
import com.bhavsar.airBnb.model.Hotel;
import com.bhavsar.airBnb.model.Room;
import com.bhavsar.airBnb.model.User;
import com.bhavsar.airBnb.model.enums.BookingStatus;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDto {

    private Long id;
    private Integer roomCount;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BookingStatus bookingStatus;
    private Set<Guest> guest;

}
