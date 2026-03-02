package com.bhavsar.airBnb.dto;

import com.bhavsar.airBnb.model.User;
import com.bhavsar.airBnb.model.enums.Gender;
import lombok.Data;

@Data
public class GuestDto {
    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;

}
