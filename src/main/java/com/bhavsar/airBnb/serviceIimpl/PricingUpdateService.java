package com.bhavsar.airBnb.serviceIimpl;

import com.bhavsar.airBnb.model.Hotel;
import com.bhavsar.airBnb.model.HotelMinPrice;
import com.bhavsar.airBnb.model.Inventory;
import com.bhavsar.airBnb.repository.HotelMinPriceRepository;
import com.bhavsar.airBnb.repository.HotelRepository;
import com.bhavsar.airBnb.repository.InventoryRepository;
import com.bhavsar.airBnb.strategy.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PricingUpdateService {
    //Schedular to update the inventory and HotelMinPrice tables every hour

    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final PricingService pricingService;
                //     second  minute  hour  day of month  month  day of week
    @Scheduled(cron = "0 0 * * * *")    // Run every five seconds
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
        log.info("Updating prices for hotel with id:{}",hotel.getId());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

       List<Inventory> inventoryList = inventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);

       updateInventoryPrices(inventoryList);
       updateHotelMinPrice(hotel, inventoryList, startDate , endDate);

    }

    //Goal is this method is to calculate the minimum price for each date for a given hotel
    // and update the HotelMinPrice table accordingly. It groups the inventory by date, finds
    // the minimum price for each date, and then updates or creates entries in the HotelMinPrice
    // table with these minimum prices.
    private void updateHotelMinPrice(Hotel hotel, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))
                )).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().orElse(BigDecimal.ZERO)));
        //prepare HotelPrice entities in bulk
        List<HotelMinPrice> hotelMinPrices = new ArrayList<>();
        dailyMinPrices.forEach((date, minPrice) -> {
            HotelMinPrice hotelMinPrice = hotelMinPriceRepository.findByHotelAndDate(hotel,date)
                    .orElse(new HotelMinPrice(hotel, date));
            hotelMinPrice.setPrice(minPrice);
            hotelMinPrices.add(hotelMinPrice);
        });
        hotelMinPriceRepository.saveAll(hotelMinPrices);
    }

    private void updateInventoryPrices(List<Inventory> inventoryList) {
        // Logic to update inventory prices based on demand, seasonality, etc.
        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPrice(inventory);
            inventory.setPrice(dynamicPrice);
        });
        inventoryRepository.saveAll(inventoryList);
    }

}
