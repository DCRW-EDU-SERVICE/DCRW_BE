package com.example.DCRW.dto;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserDto {
    private String userName;
    private LocalDate birthDate;
    private String address;
    private int role;
}
