package com.bhavsar.airBnb.repository;

import com.bhavsar.airBnb.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}