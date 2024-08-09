package com.example.DCRW.dto;

import java.util.Date;

public class UserDto {
    private String userId, password, name, address;
    private Date birthDate;
    private String teacherCode;

//    private String provider, providerId; // 구글 OAuth는 나중에...


    public UserDto(String userId, String password, String name, String address, Date birthDate, String teacherCode) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.address = address;
        this.birthDate = birthDate;
        this.teacherCode = teacherCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }
}
