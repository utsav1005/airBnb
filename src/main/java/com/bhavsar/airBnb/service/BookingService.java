package com.bhavsar.airBnb.service;

import com.bhavsar.airBnb.dto.BookingDto;
import com.bhavsar.airBnb.dto.BookingRequest;
import com.bhavsar.airBnb.dto.GuestDto;

import java.util.List;

public interface BookingService {
    BookingDto initializeBooking(BookingRequest bookingRequest);
    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);
}
