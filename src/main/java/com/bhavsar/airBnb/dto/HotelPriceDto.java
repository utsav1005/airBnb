package com.bhavsar.airBnb.dto;

import com.bhavsar.airBnb.model.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelPriceDto {
    private Hotel hotel;
    private Double price;


}
