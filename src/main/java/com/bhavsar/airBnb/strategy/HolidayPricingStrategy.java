package com.bhavsar.airBnb.strategy;

import com.bhavsar.airBnb.model.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy {
    private final PricingStrategy wrapped;
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        boolean isHoliday = true;
        if(isHoliday){
            price = price.multiply(BigDecimal.valueOf(1.25)); // 25% increase on holidays
        }
        return price;
    }
}
