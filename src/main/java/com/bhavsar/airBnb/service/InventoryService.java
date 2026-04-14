package com.bhavsar.airBnb.service;

import com.bhavsar.airBnb.dto.HotelPriceDto;
import com.bhavsar.airBnb.dto.HotelSearchRequest;
import com.bhavsar.airBnb.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface InventoryService {

    void initializeRoomForAYear(Room room);
    void deleteFutureInventories(Room room);


    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
