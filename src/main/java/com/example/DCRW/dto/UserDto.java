package com.example.DCRW.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserDto {

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Pattern(regexp = "^[a-z0-9]{4,20}$", message = "아이디는 영문 소문자와 숫자 4~20자리여야 합니다.")
    private String userId;

    private String password;
    private String name;
    private String address;
    @JsonProperty("birthdate")
    private Date birthDate;
    private String teacherCode;
    private Date joinDate;

//    private String provider, providerId; // 구글 OAuth는 나중에...


    public UserDto(String userId, String password, String name, String address, Date birthDate, String teacherCode) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.address = address;
        this.birthDate = birthDate;
        this.teacherCode = teacherCode;
    }

}
