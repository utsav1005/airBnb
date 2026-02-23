package com.bhavsar.airBnb.service;


import com.bhavsar.airBnb.dto.HotelDto;

public interface HotelService {
    HotelDto createHotel(HotelDto hotelDto);
    HotelDto getHotelById(Long id);
    HotelDto updateHotelsById(Long id , HotelDto dto);
    void deleteHotelById(Long id);
}
