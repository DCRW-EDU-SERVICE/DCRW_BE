package com.example.DCRW.controller;

import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.course.TeacherCourseDto;
import com.example.DCRW.dto.user.CustomUserDetails;
import com.example.DCRW.service.course.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class CourseController {
    private final CourseService courseService;
    @GetMapping("/course")
    public ResponseEntity<ResultDto<String>> teacherCoursePage(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        TeacherCourseDto teacherCourseDto = courseService.teacherCourse(customUserDetails.getUsername());

        ResultDto resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("선생님 강의 관리 페이지 조회 성공")
                .data(teacherCourseDto)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }
}
