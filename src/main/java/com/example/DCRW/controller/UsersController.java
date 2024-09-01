package com.example.DCRW.controller;


import com.example.DCRW.dto.CustomUserDetails;
import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.RegisterDto;
import com.example.DCRW.dto.UserDto;
import com.example.DCRW.service.ProfileService;
import com.example.DCRW.service.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UsersController {

    private final RegisterService registerService;
    private final ProfileService profileService;


    @Operation(summary = "회원가입", description = "회원가입 시 사용하는 API")
    @PostMapping("/signup")
    public ResponseEntity<ResultDto<String>> signUp(@Valid @RequestBody RegisterDto registerDto) {
        // @Valid 사용으로 유효성 검사 실패하면 MethodArgumentNotValidException 예외 발생
        System.out.println("user dto : " + registerDto);
        ResultDto resultDto = registerService.register(registerDto);

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    @GetMapping("/user/profile")
    public ResponseEntity<ResultDto<UserDto>> userProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        UserDto userDto = profileService.showProfile(customUserDetails.getUsername());

        ResultDto<UserDto> resultDto = ResultDto.res(HttpStatus.OK, "회원정보조회 성공", userDto);

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }


}