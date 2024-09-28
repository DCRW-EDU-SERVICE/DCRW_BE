package com.example.DCRW.controller;

import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.course.*;
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
    // 교사 페이지 -> 학생 누르면 나오는 강의 콘텐츠 조회
    @PostMapping("/course/contents")
    public ResponseEntity<ResultDto<Object>> showContents(@RequestBody ContentDto contentDto){
        CustomUserDetails customUserDetails = getUsername(SecurityContextHolder.getContext().getAuthentication());

        ContentResponseDto contentResponseDto = contentsService.showContentsTeacher(contentDto, customUserDetails.getUsername());

        ResultDto<Object> resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("강의 콘텐츠 조회 성공")
                .data(contentResponseDto)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    // 학생 페이지 -> 강의 콘텐츠 조회
    @GetMapping("/course/{courseId}/contents")
    public ResponseEntity<ResultDto<Object>> showContentsStudent(@PathVariable(name = "courseId") int courseId){
        CustomUserDetails customUserDetails = getUsername(SecurityContextHolder.getContext().getAuthentication());

        ContentResponseDto contentResponseDto = contentsService.showContentsStudent(courseId, customUserDetails.getUsername());

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
            @RequestPart(value = "content") ContentAddDto contentAddDto,
            @RequestPart(value = "file", required = false) List<MultipartFile> files) throws IOException {

        CustomUserDetails customUserDetails = getUsername(SecurityContextHolder.getContext().getAuthentication());

        List<ContentFileResponseDto> contentList = contentsService.addContents(customUserDetails.getUsername(), files, contentAddDto, "content");

        ResultDto<Object> resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("강의콘텐츠 추가 성공")
                .data(contentList)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    // 강의 콘텐츠 삭제
    @DeleteMapping("/contents/{contentsID}")
    public ResponseEntity<ResultDto<String>> deleteContents(@PathVariable(name = "contentsID") int contentId){
        CustomUserDetails customUserDetails = getUsername(SecurityContextHolder.getContext().getAuthentication());
        contentsService.deleteContents(contentId, customUserDetails.getUsername());

        ResultDto<String> resultDto = ResultDto.res(HttpStatus.OK, "강의콘텐츠 삭제 성공");

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    // 세션 username 추출
    private CustomUserDetails getUsername(Authentication authentication){
        return (CustomUserDetails) authentication.getPrincipal();
    }
}
