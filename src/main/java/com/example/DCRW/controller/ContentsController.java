package com.example.DCRW.controller;

import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.course.ContentAddDto;
import com.example.DCRW.dto.course.ContentDto;
import com.example.DCRW.dto.course.ContentResponseDto;
import com.example.DCRW.dto.course.CourseFileDto;
import com.example.DCRW.dto.user.CustomUserDetails;
import com.example.DCRW.service.course.ContentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class ContentsController {

    private final ContentsService contentsService;
    // 학생 강의 콘텐츠 조회
    @PostMapping("/course/contents")
    public ResponseEntity<ResultDto<Object>> showContents(@RequestBody ContentDto contentDto){
        ContentResponseDto contentResponseDto = contentsService.showContents(contentDto);

        ResultDto<Object> resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("강의 콘텐츠 조회 성공")
                .data(contentResponseDto)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    // 강의 콘텐츠 추가
    @PostMapping("/contents")
    public ResponseEntity<ResultDto<Object>> addContents(
            @RequestPart ContentAddDto contentAddDto,
            @RequestPart(value = "file", required = false) List<MultipartFile> files) throws IOException {

        CustomUserDetails customUserDetails = getUsername(SecurityContextHolder.getContext().getAuthentication());

        List<CourseFileDto> contentList = contentsService.addContents(customUserDetails.getUsername(), files, contentAddDto, "content");

        ResultDto<Object> resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("강의콘텐츠 추가 성공")
                .data(contentList)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    // 세션 username 추출
    private CustomUserDetails getUsername(Authentication authentication){
        return (CustomUserDetails) authentication.getPrincipal();
    }
}
