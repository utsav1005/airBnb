package com.bhavsar.airBnb.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class HotelContactInfo {

    private String address;

    @NotBlank(message = "Phone is required")
    private String phoneNumber;

    @Email(message = "Invalid email")
    private String email;
    private String location;
}
