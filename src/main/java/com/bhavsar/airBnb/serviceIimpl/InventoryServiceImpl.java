package com.bhavsar.airBnb.serviceIimpl;

import com.bhavsar.airBnb.dto.HotelDto;
import com.bhavsar.airBnb.dto.HotelPriceDto;
import com.bhavsar.airBnb.dto.HotelSearchRequest;
import com.bhavsar.airBnb.model.Hotel;
import com.bhavsar.airBnb.model.HotelMinPrice;
import com.bhavsar.airBnb.model.Inventory;
import com.bhavsar.airBnb.model.Room;
import com.bhavsar.airBnb.repository.HotelMinPriceRepository;
import com.bhavsar.airBnb.repository.InventoryRepository;
import com.bhavsar.airBnb.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;

    @Transactional
    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1); //1Year
        LocalDate currentDate = today;
        List<Inventory> inventories = new ArrayList<>();
       while(!currentDate.isAfter(endDate)){
           Inventory inventory = Inventory.builder()
                   .hotel(room.getHotel())
                   .room(room)
                   .bookedCount(0L)
                   .city(room.getHotel().getCity())
                   .date(currentDate)
                   .price(room.getBasePrice())
                   .surgeFactor(BigDecimal.ONE)
                   .totalCount(room.getTotalCount())
                   .closed(false)
                   .reservedCount(0L)
                   .build();
         inventories.add(inventory);
         currentDate = currentDate.plusDays(1);
       }
        inventoryRepository.saveAll(inventories);
    }
    @Override
    @Transactional
    public void deleteFutureInventories(Room room) {
        LocalDate date  = LocalDate.now();
        inventoryRepository.deleteByRoomAndDateAfter(room,date);
    }

    @Override
    public Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest) {
        log.info("Searching hotels in city {} from {} to {} for {} rooms",hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),hotelSearchRequest.getRoomsCount());

        PageRequest pageRequest = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());
        long dateCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate() ,
                hotelSearchRequest.getEndDate()) + 1;


        //business Logic 90 days
       return hotelMinPriceRepository.findHotelWithAvailableInventory(hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                 dateCount, pageRequest
      );

    }
}
