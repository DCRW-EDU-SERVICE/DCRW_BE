package com.example.DCRW.controller;


import com.example.DCRW.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class RegisterController {

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody UserDto userDto){


        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}
