package com.bhavsar.airBnb.dto;

import com.bhavsar.airBnb.model.HotelContactInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class HotelDto {
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50)
    private String name;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50)
    private String city;

    private String[] photos;

    private String[] amenities;

    @NotNull(message =  "contact info is required")
    @Valid
    private HotelContactInfo contactInfo;

    private Boolean active;
}
