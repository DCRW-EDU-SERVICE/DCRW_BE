package com.example.DCRW.controller;


import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.user.*;
import com.example.DCRW.entity.Users;
import com.example.DCRW.service.user.ProfileService;
import com.example.DCRW.service.user.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Optional;

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

    // 회원정보 조회
    @GetMapping("/user/profile")
    public ResponseEntity<ResultDto<UserDto>> userProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        UserDto userDto = profileService.showProfile(customUserDetails.getUsername());
        ResultDto<UserDto> resultDto = ResultDto.res(HttpStatus.OK, "회원정보조회 성공", userDto);

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    // 회원정보 수정
    @PatchMapping("/user/profile")
    public ResponseEntity<ResultDto<UserUpdateDto>> updateUser(@RequestBody UserUpdateDto userUpdateDto) throws SQLException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Users users = profileService.updateProfile(customUserDetails.getUsername(), userUpdateDto);

        userUpdateDto = UserUpdateDto.builder()
                .birthDate(Optional.ofNullable(users.getBirthDate())) // Optional<LocalDate>로 반환해야 하므로 감싸서 반환
                .address(Optional.ofNullable(users.getAddress()))
                .userName(Optional.ofNullable(users.getName()))
                .build();

        ResultDto resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("회원정보수정 성공")
                .data(userUpdateDto)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }


    // 회원 탈퇴
    @DeleteMapping("/user/profile")
    public ResponseEntity<ResultDto<UserDto>> deleteUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        profileService.deleteProfile(customUserDetails.getUsername());

        ResultDto resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("회원정보삭제 성공")
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

}