package com.bhavsar.airBnb.service;


import com.bhavsar.airBnb.dto.HotelDto;
import com.bhavsar.airBnb.dto.HotelInfoDto;

import java.util.List;

public interface HotelService {
    HotelDto createHotel(HotelDto hotelDto);
    HotelDto getHotelById(Long id);
    List<HotelDto> getAllHotel();
    HotelDto updateHotelsById(Long id , HotelDto dto);
    void deleteHotelById(Long id);
    void activeHotel(Long id);

    HotelInfoDto getHotelInfoById(Long hotelId);
}
