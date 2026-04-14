package com.bhavsar.airBnb.controller;

import com.bhavsar.airBnb.dto.HotelDto;
import com.bhavsar.airBnb.dto.HotelInfoDto;
import com.bhavsar.airBnb.dto.HotelPriceDto;
import com.bhavsar.airBnb.dto.HotelSearchRequest;
import com.bhavsar.airBnb.service.HotelService;
import com.bhavsar.airBnb.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {
    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotels(
            @RequestParam String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam Long roomsCount,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "9") Integer size) {

        // Build request object manually
        HotelSearchRequest hotelSearchRequest = new HotelSearchRequest();
        hotelSearchRequest.setCity(city);
        hotelSearchRequest.setStartDate(startDate);
        hotelSearchRequest.setEndDate(endDate);
        hotelSearchRequest.setRoomsCount(roomsCount);
        hotelSearchRequest.setPage(page);
        hotelSearchRequest.setSize(size);

        Page<HotelPriceDto> result = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }
}
