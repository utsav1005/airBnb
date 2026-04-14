package com.bhavsar.airBnb.serviceIimpl;

import com.bhavsar.airBnb.dto.RoomDto;
import com.bhavsar.airBnb.exception.ResourceNotFoundException;
import com.bhavsar.airBnb.model.Hotel;
import com.bhavsar.airBnb.model.Room;
import com.bhavsar.airBnb.repository.HotelRepository;
import com.bhavsar.airBnb.repository.InventoryRepository;
import com.bhavsar.airBnb.repository.RoomRepository;
import com.bhavsar.airBnb.service.InventoryService;
import com.bhavsar.airBnb.service.RoomService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final InventoryRepository inventoryRepository;

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final  InventoryService inventoryService;
    private final ModelMapper modelmapper;

    @Override
    @Transactional
    public RoomDto createNewRoom(Long hotelId , RoomDto roomDto) {
        log.info("Creating a new room with hotelId:{}",hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id:"+hotelId));
        Room room = modelmapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);
//        TODO : create a inventory as soons as is created and if hotel is active.
        log.info("Successfully Creates Room With id:{}",room.getId());
        if(hotel.getActive()){
            inventoryService.initializeRoomForAYear(room);
        }

        return modelmapper.map(room , RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomInHotel(Long hotelId) {
        log.info("find hotel by id:{}",hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id" + hotelId));
        log.info("Getting All Rooms in Hotel with Id:{}",hotelId);

        return hotel.getRooms()
                .stream()
                .map(rooms-> modelmapper.map(rooms,RoomDto.class))
                .toList();
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting the room with id:{}",roomId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id:" + roomId));
        return modelmapper.map(room , RoomDto.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("Deleting the room with id:{}",roomId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id:" + roomId));
        inventoryRepository.deleteByRoom(room); //delete all inventory
       roomRepository.delete(room);   //then remove parent
        // TODO : create a delete Inventory in future

    }
}
