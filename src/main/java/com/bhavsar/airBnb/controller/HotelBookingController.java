package com.bhavsar.airBnb.controller;

import com.bhavsar.airBnb.dto.BookingDto;
import com.bhavsar.airBnb.dto.BookingRequest;
import com.bhavsar.airBnb.dto.GuestDto;
import com.bhavsar.airBnb.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingService bookingService;
    @PostMapping("/init")
    public ResponseEntity<BookingDto> initializeBooking(@RequestBody BookingRequest bookingRequest){
        return ResponseEntity.ok(bookingService.initializeBooking(bookingRequest));
    }
    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId ,  @Valid @RequestBody List<GuestDto> guestDtoList){
        return ResponseEntity.ok(bookingService.addGuests(bookingId , guestDtoList));
    }
    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<Map<String , String >> initiatePayment(@PathVariable Long bookingId){
       String sessionUrl =  bookingService.initiatePayments(bookingId);
       return ResponseEntity.ok(Map.of("sessionUrl",sessionUrl));
    }
 }
