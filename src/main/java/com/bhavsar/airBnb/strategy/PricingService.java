package com.bhavsar.airBnb.strategy;

import com.bhavsar.airBnb.model.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PricingService {
    public BigDecimal calculateDynamicPrice(Inventory inventory) {

        PricingStrategy priceStrategy = new BasePriceStrategy();
        priceStrategy = new SurgePriceStrategy(priceStrategy);

        priceStrategy = new OccupancyPricingStrategy(priceStrategy);
        priceStrategy = new HolidayPricingStrategy(priceStrategy);
        priceStrategy = new UrgencyPricingStrategy(priceStrategy);

        return priceStrategy.calculatePrice(inventory);
    }
}
