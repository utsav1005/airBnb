package com.bhavsar.airBnb.dto;

import com.bhavsar.airBnb.model.User;
import com.bhavsar.airBnb.model.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class GuestDto {
    private Long id;

    @NotNull(message = "User is required")
    private User user;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50)
    private String name;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Age is required")
    @Min(0)
    @Max(100)
    private Integer age;

}
