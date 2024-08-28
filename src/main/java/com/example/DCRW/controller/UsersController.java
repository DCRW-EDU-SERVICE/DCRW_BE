package com.example.DCRW.controller;


import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.UserDto;
import com.example.DCRW.service.RegisterService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class UsersController {
    private final RegisterService registerService;

    public UsersController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<ResultDto<String>> signUp(@RequestBody @Valid UserDto userDto) {
        registerService.Register(userDto);


        return new ResponseEntity<>(ResultDto.res(HttpStatus.OK, "회원가입 성공"), HttpStatus.OK);
    }

}