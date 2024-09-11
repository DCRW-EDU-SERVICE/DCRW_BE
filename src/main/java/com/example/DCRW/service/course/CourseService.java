package com.example.DCRW.service.course;

import com.example.DCRW.dto.course.CourseAddDto;
import com.example.DCRW.dto.course.CourseUpdateDto;
import com.example.DCRW.dto.course.TeacherCourseDto;
import com.example.DCRW.entity.Course;

import java.util.Map;

public interface CourseService {
    TeacherCourseDto teacherCourse(String username);

    Map<String, String> addTeacherCourse(CourseAddDto courseAddDto, String username);

    Course updateCourse(int courseId, CourseUpdateDto courseUpdateDto, String username);

    void deleteCourse(int courseId, String username);
}
