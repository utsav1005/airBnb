package com.bhavsar.airBnb.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.Valid;
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
@Valid
public class HotelContactInfo {

    private String address;
    private String contactPersonName;
    @NotBlank(message = "Phone is required")
    private String phoneNumber;

    @Email(message = "Invalid email")
    private String email;
    private String location;
}
