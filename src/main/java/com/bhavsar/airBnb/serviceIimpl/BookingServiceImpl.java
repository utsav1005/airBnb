package com.bhavsar.airBnb.serviceIimpl;

import com.bhavsar.airBnb.dto.BookingDto;
import com.bhavsar.airBnb.dto.BookingRequest;
import com.bhavsar.airBnb.dto.GuestDto;
import com.bhavsar.airBnb.exception.ResourceNotFoundException;
import com.bhavsar.airBnb.exception.UnAuthorizedException;
import com.bhavsar.airBnb.model.*;
import com.bhavsar.airBnb.model.enums.BookingStatus;
import com.bhavsar.airBnb.repository.*;
import com.bhavsar.airBnb.service.BookingService;
import com.bhavsar.airBnb.service.CheckoutService;
import com.bhavsar.airBnb.service.HotelService;
import com.bhavsar.airBnb.service.RoomService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final GuestRepository guestRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final HotelService hotelService;
    private final RoomService roomService;
    private final InventoryRepository inventoryRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;
    private final CheckoutService checkoutService;

    @Value("${frontend.url}")
    private String frontEndUrl;

    @Override
    @Transactional
    public BookingDto initializeBooking(BookingRequest bookingRequest) {
        log.info("initializing booking for hotel : {} , room: {} , date{}",bookingRequest.getHotelId(),bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate());
        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id" + bookingRequest.getHotelId()));

        Room room = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id" + bookingRequest.getRoomId()));

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(
                room.getId(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());
        if(inventoryList.size() != daysCount){
            throw new IllegalStateException("Room is not available ");
        }

        //Reserve the room
        for(Inventory inventory : inventoryList){
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }
        inventoryRepository.saveAll(inventoryList);


        //TODO : calculate dynamic amount
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomCount(bookingRequest.getRoomsCount())
                .amount(BigDecimal.TEN)
                .build();

        booking = bookingRepository.save(booking);
        return modelMapper.map(booking , BookingDto.class);

    }
    @Override
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {
        log.info("Adding guests for  booking with id : {} ",bookingId);
        Booking  booking  = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id" + bookingId));

        User currentUser = getCurrentUser();
        if(!currentUser.equals(booking.getUser())){
            throw new UnAuthorizedException("User is not authorized to add guests to this booking with userId:"+currentUser.getId());
        }

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired");
        }
        if(booking.getBookingStatus() != BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not under reserved state , cannot add guests");
        }
        for(GuestDto guestDto : guestDtoList){
            Guest guest = modelMapper.map(guestDto, Guest.class);
            guestDto.setUser(currentUser);
            guest = guestRepository.save(guest);
            booking.getGuest().add(guest);
        }
        booking.setBookingStatus(BookingStatus.GUEST_ADDED);
        Booking save = bookingRepository.save(booking);

        return modelMapper.map(save , BookingDto.class);
    }
    public boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser(){
     return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public String initiatePayments(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id" + bookingId));
        User currentUser = getCurrentUser();
        if(!currentUser.equals(booking.getUser())){
            throw new UnAuthorizedException("User is not authorized to add guests to this booking with userId:"+currentUser.getId());
        }

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired");
        }

       String sessionUrl =  checkoutService
               .getCheckoutSession(booking , frontEndUrl+"payments/success" , frontEndUrl+"payments/failure");
        booking.setBookingStatus(BookingStatus.PAYMENTS_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }
}
