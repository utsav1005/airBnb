package com.bhavsar.airBnb.serviceIimpl;

import com.bhavsar.airBnb.dto.HotelDto;
import com.bhavsar.airBnb.dto.HotelInfoDto;
import com.bhavsar.airBnb.dto.RoomDto;
import com.bhavsar.airBnb.exception.ResourceNotFoundException;
import com.bhavsar.airBnb.model.Hotel;
import com.bhavsar.airBnb.model.Room;
import com.bhavsar.airBnb.model.User;
import com.bhavsar.airBnb.repository.HotelRepository;
import com.bhavsar.airBnb.repository.RoomRepository;
import com.bhavsar.airBnb.service.HotelService;
import com.bhavsar.airBnb.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public HotelDto createHotel(HotelDto hotelDto) {
        log.info("Creating a new hotel with name {} ",hotelDto.getName());
        Hotel hotel = new Hotel();

        // Map fields from DTO to Hotel entity
        hotel.setName(hotelDto.getName());
        hotel.setCity(hotelDto.getCity());
        hotel.setPhotos(hotelDto.getPhotos());
        hotel.setAmenities(hotelDto.getAmenities());
        hotel.setContactInfo(hotelDto.getContactInfo());
        hotel.setActive(false);

        // Set the current user as owner
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(currentUser);

        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Created a new Hotel with Id:{}",savedHotel.getId());
        return modelMapper.map(savedHotel , HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting the hotel with id:{}",id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id:"+id));
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(hotel.getOwner() == null || !currentUser.equals(hotel.getOwner())){
            throw new ResourceNotFoundException("This user does not own this hotel with id:"+id);
        }
        return modelMapper.map(hotel , HotelDto.class);
    }

    @Override
    public Page<HotelDto> findAllHotel(Pageable pageable) {
        Page<Hotel> hotelPage = hotelRepository.findAll(pageable);
        return hotelPage.map(hotel -> modelMapper.map(hotel, HotelDto.class));
    }

    @Override
    @Transactional
    public HotelDto updateHotelsById(Long id, HotelDto dto) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel id is not found with ID"+id));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Only owner can update their hotel
        if(hotel.getOwner() == null || !hotel.getOwner().equals(currentUser)){
            throw new ResourceNotFoundException("This user does not own this hotel with id:"+id);
        }

        // Update only the allowed fields from DTO, preserve owner and id
        hotel.setName(dto.getName());
        hotel.setCity(dto.getCity());
        hotel.setPhotos(dto.getPhotos());
        hotel.setAmenities(dto.getAmenities());
        hotel.setContactInfo(dto.getContactInfo());
        // Note: Do not update 'active' status through this endpoint
        // Note: Owner is never updated

        Hotel saveHotel = hotelRepository.save(hotel);
        return modelMapper.map(saveHotel, HotelDto.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel id is not found with ID"+id));
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(hotel.getOwner() == null || !hotel.getOwner().equals(currentUser)){
            throw new ResourceNotFoundException("This user does not own this hotel with id:"+id);
        }

        //TODO: delete the future inventories for this hotel
        for(Room rooms: hotel.getRooms()){
            inventoryService.deleteFutureInventories(rooms);
//            roomRepository.delete(rooms);  don't write it because it's handle by Hotel Parent Entity
        }
        hotelRepository.deleteById(id);

    }

    @Override
    @Transactional
    public void activeHotel(Long id) {
        log.info("Activating Hotel With id{}",id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id" + id));
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(hotel.getOwner() == null || !hotel.getOwner().equals(currentUser)){
            throw new ResourceNotFoundException("This user does not own this hotel with id:"+id);
        }

        hotel.setActive(true);
        hotelRepository.save(hotel);

        // ✅ Fetch rooms explicitly
        List<Room> rooms = roomRepository.findByHotel(hotel);
        log.info("Found {} rooms for hotel {}", rooms.size(), hotel.getId());

        if (rooms.isEmpty()) {
            log.warn("No rooms found for hotel {}. Add rooms first!", hotel.getId());
            return;
        }
        //TODO: Create Hotel Inventory for this activated Hotels
        for(Room room: hotel.getRooms()){
            inventoryService.initializeRoomForAYear(room);
        }

        log.info("Inventory initialized for hotel {}", hotel.getId());

    }
    //Public Method
    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel id is not found with ID"+hotelId));

        List<RoomDto> rooms = hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .toList();
        return new HotelInfoDto(modelMapper.map(hotel,HotelDto.class),rooms);

    }
}
