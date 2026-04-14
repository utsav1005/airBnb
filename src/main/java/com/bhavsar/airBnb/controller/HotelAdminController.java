package com.bhavsar.airBnb.controller;

import com.bhavsar.airBnb.dto.HotelDto;
import com.bhavsar.airBnb.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class
HotelAdminController {
    private final HotelService hotelService;

    @PreAuthorize("hasRole('HOTEL_MANAGER')")
    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@Valid @RequestBody HotelDto dto){
        log.info("Attempting to create new Hotel{}",dto.getName());
        HotelDto hotel = hotelService.createHotel(dto);
        return new ResponseEntity<>(hotel,HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('HOTEL_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long id){
        log.info("Fetching hotel data with id:{}",id);
        HotelDto hotelById = hotelService.getHotelById(id);
        return new ResponseEntity<>(hotelById,HttpStatus.ACCEPTED);
    }

    @GetMapping
    public ResponseEntity<Page<HotelDto>> getAllHotel(@PageableDefault(size = 5 , sort = "name" , direction = Sort.Direction.ASC) Pageable pageable){
        Page<HotelDto> hotelList = hotelService.findAllHotel(pageable);
        return new ResponseEntity<>(hotelList,HttpStatus.ACCEPTED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HOTEL_MANAGER')")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long id , @Valid @RequestBody HotelDto dto){
        HotelDto hotel = hotelService.updateHotelsById(id,dto);
        return new ResponseEntity<>(hotel,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('HOTEL_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable Long id ){
        hotelService.deleteHotelById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('HOTEL_MANAGER')")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> activateHotel(@PathVariable Long id) {
        hotelService.activeHotel(id);
        return ResponseEntity.noContent().build();
    }

}
