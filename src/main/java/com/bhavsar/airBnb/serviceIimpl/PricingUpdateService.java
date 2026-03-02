package com.bhavsar.airBnb.serviceIimpl;

import com.bhavsar.airBnb.model.Hotel;
import com.bhavsar.airBnb.model.Inventory;
import com.bhavsar.airBnb.repository.HotelMinPriceRepository;
import com.bhavsar.airBnb.repository.HotelRepository;
import com.bhavsar.airBnb.repository.InventoryRepository;
import com.bhavsar.airBnb.strategy.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingUpdateService {
    //Schedular to update the inventory and HotelMinPrice tables every hour

    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final PricingService pricingService;

    public void updatePrices() {
        // Logic to update inventory and HotelMinPrice tables
        int page = 0;
        int batchSize = 100;

        while(true){
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(page, batchSize));
                if(hotelPage.isEmpty()){
                    break;
                }
                hotelPage.getContent().forEach(this::updateHotelPrices);
              page++;
        }

    }
    private void updateHotelPrices(Hotel hotel) {
        // Logic to update HotelMinPrice table based on the latest inventory prices
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

       List<Inventory> inventoryList = inventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);

       updateHotelMinPrice(hotel, inventoryList, startDate , endDate);

    }

    private void updateHotelMinPrice(Hotel hotel, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {

    }

    @Transactional
    private void updateInventoryPrices(List<Inventory> inventoryList) {
        // Logic to update inventory prices based on demand, seasonality, etc.
        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPrice(inventory);
            inventory.setPrice(dynamicPrice);

        });
        inventoryRepository.saveAll(inventoryList);
    }

}
