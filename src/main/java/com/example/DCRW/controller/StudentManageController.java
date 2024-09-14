package com.example.DCRW.controller;

import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.user.CustomUserDetails;
import com.example.DCRW.dto.user.student.StudentCourseDto;
import com.example.DCRW.dto.user.student.StudentSearchDto;
import com.example.DCRW.service.user.student.StudentManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class StudentManageController {

    private final StudentManageService studentManageService;

    // 학생 검색
    @PostMapping("/student-management")
    public ResponseEntity<ResultDto<Object>> searchStudent(@RequestBody StudentSearchDto studentSearchDto){


        String name = studentManageService.searchStudentList(studentSearchDto);
        Map<String, String> map = new HashMap<>();
        map.put("studentId", studentSearchDto.getStudentId());
        map.put("studentName", name);

        ResultDto<Object> resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("조회 성공")
                .data(map)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    // 학생 강의 등록
    @PostMapping("/student-management/register")
    public ResponseEntity<ResultDto<String>> studentRegister(@RequestBody StudentCourseDto studentCourseDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        studentManageService.studentRegister(customUserDetails.getUsername(), studentCourseDto);

        ResultDto<String> resultDto = ResultDto.res(HttpStatus.OK, "등록 성공");

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

//    // 학생 강의 삭제
//    @DeleteMapping("/student-management")
//    public ResponseEntity<ResultDto<String>> studentDelete(@RequestBody )

}
