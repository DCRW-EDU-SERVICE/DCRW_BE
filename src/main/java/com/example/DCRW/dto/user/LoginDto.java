package com.example.DCRW.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

public class LoginDto {
    @JsonProperty("username")
    private String userName;
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginDto() {}
}
