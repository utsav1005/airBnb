package com.bhavsar.airBnb.strategy;

import com.bhavsar.airBnb.model.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(Inventory inventory);
}
