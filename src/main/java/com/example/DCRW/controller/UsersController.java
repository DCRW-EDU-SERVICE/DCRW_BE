package com.example.DCRW.controller;


import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.UserDto;
import com.example.DCRW.service.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Tag(name = "User", description = "User API")
public class UsersController {
    private final RegisterService registerService;

    public UsersController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Operation(summary = "회원가입", description = "회원가입 시 사용하는 API")
    @PostMapping("/signup")
    public ResponseEntity<ResultDto<String>> signUp(@Valid @RequestBody UserDto userDto) {
        // @Valid 사용으로 유효성 검사 실패하면 MethodArgumentNotValidException 예외 발생
        System.out.println("user dto : " + userDto);
        ResultDto resultDto = registerService.register(userDto);

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }


}