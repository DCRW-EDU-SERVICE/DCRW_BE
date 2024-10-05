package com.example.DCRW.controller;

import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.course.CourseAddDto;
import com.example.DCRW.dto.course.CourseUpdateDto;
import com.example.DCRW.dto.course.TeacherCourseDto;
import com.example.DCRW.dto.course.plan.CoursePlanDto;
import com.example.DCRW.dto.user.CustomUserDetails;
import com.example.DCRW.entity.Course;
import com.example.DCRW.entity.CoursePlan;
import com.example.DCRW.service.course.CourseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class CourseController {
    private final CourseService courseService;

    // 교사 강의 관리 페이지, 학생 관리 페이지
    @GetMapping("/course")
    public ResponseEntity<ResultDto<String>> teacherCoursePage(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        TeacherCourseDto teacherCourseDto = courseService.teacherCourse(customUserDetails.getUsername());

        ResultDto resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("조회 성공")
                .data(teacherCourseDto)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    // 강의 생성
    @PostMapping("/course")
    public ResponseEntity<ResultDto<String>> addTeacherCourse(@RequestBody CourseAddDto courseAddDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Map<String, String> map = courseService.addTeacherCourse(courseAddDto, customUserDetails.getUsername());

        ResultDto resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("강의 생성 성공")
                .data(map)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    // 강의 수정
    @PatchMapping("/course/{courseID}")
    public ResponseEntity<ResultDto<CourseAddDto>> updateCourse(@PathVariable("courseID") int courseId, @RequestBody CourseUpdateDto courseUpdateDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Course course = courseService.updateCourse(courseId, courseUpdateDto, customUserDetails.getUsername());

        // Course 객체를 CourseAddDto로 변환
        List<CoursePlanDto> coursePlanDtos = new ArrayList<>();
        for (CoursePlan coursePlan : course.getCoursePlanList()) {
            CoursePlanDto coursePlanDto = new CoursePlanDto(coursePlan.getWeek(), coursePlan.getContent());
            coursePlanDtos.add(coursePlanDto);
        }

        CourseAddDto courseAddDto = CourseAddDto.builder()
                .title(course.getTitle())
                .coursePlanList(coursePlanDtos)
                .build();

        // ResultDto 생성
        ResultDto<CourseAddDto> resultDto = ResultDto.<CourseAddDto>builder()
                .status(HttpStatus.OK)
                .message("강의 수정 성공")
                .data(courseAddDto)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    // 강의 삭제
    @DeleteMapping("/course/{courseID}")
    public ResponseEntity<ResultDto<String>> deleteCourse(@PathVariable("courseID") int courseId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        courseService.deleteCourse(courseId, customUserDetails.getUsername());

        ResultDto<String> resultDto = ResultDto.res(HttpStatus.OK, "강의 삭제 성공");

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }
}
