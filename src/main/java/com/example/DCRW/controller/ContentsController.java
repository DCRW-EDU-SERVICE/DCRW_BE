package com.example.DCRW.controller;

import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.course.ContentDto;
import com.example.DCRW.dto.course.ContentResponseDto;
import com.example.DCRW.service.course.ContentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
