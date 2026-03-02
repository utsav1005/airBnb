package com.bhavsar.airBnb.strategy;

import com.bhavsar.airBnb.model.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OccupancyPricingStrategy implements PricingStrategy {
    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        double occupancyRate = (double) inventory.getBookedCount() / inventory.getTotalCount();
        if (occupancyRate > 0.8) {
            price = price.multiply(BigDecimal.valueOf(1.2)); // 20% increase
        } else if (occupancyRate > 0.5) {
            price = price.multiply(BigDecimal.valueOf(1.1)); // 10% increase

    }
        return price;
    }
}
