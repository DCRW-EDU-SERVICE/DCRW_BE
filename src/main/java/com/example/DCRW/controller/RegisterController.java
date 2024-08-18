package com.example.DCRW.controller;


import com.example.DCRW.dto.UserDto;
import com.example.DCRW.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class RegisterController {
    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody UserDto userDto){
        registerService.Register(userDto);

        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}
