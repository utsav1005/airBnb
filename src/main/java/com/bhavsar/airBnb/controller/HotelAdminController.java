package com.bhavsar.airBnb.controller;

import com.bhavsar.airBnb.dto.HotelDto;
import com.bhavsar.airBnb.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelAdminController {
    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@RequestBody HotelDto dto){
        log.info("Attempting to create new Hotel{}",dto.getName());
        HotelDto hotel = hotelService.createHotel(dto);
        return new ResponseEntity<>(hotel,HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long id){
        log.info("Fetching hotel data with id:{}",id);
        HotelDto hotelById = hotelService.getHotelById(id);
        return new ResponseEntity<>(hotelById,HttpStatus.ACCEPTED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long id , @RequestBody HotelDto dto){
        HotelDto hotel = hotelService.updateHotelsById(id,dto);
        return new ResponseEntity<>(hotel,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable Long id ){
        hotelService.deleteHotelById(id);
        return ResponseEntity.noContent().build();
    }
}
