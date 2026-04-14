package com.bhavsar.airBnb.strategy;

import com.bhavsar.airBnb.model.Inventory;

import java.math.BigDecimal;

public class BasePriceStrategy implements  PricingStrategy {
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();
    }
}
