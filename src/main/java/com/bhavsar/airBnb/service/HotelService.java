package com.bhavsar.airBnb.service;


import com.bhavsar.airBnb.dto.HotelDto;
import com.bhavsar.airBnb.dto.HotelInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HotelService {
    HotelDto createHotel(HotelDto hotelDto);
    HotelDto getHotelById(Long id);
    Page<HotelDto> findAllHotel(Pageable pageable);
    HotelDto updateHotelsById(Long id , HotelDto dto);
    void deleteHotelById(Long id);
    void activeHotel(Long id);

    HotelInfoDto getHotelInfoById(Long hotelId);
}
