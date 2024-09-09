package com.example.DCRW.service.course;

import com.example.DCRW.dto.course.CourseAddDto;
import com.example.DCRW.dto.course.TeacherCourseDto;

import java.util.Map;

public interface CourseService {
    TeacherCourseDto teacherCourse(String username);

    Map<String, String> addTeacherCourse(CourseAddDto courseAddDto, String username);
}
