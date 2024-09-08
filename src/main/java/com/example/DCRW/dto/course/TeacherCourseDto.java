package com.example.DCRW.dto.course;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class TeacherCourseDto {
    private String teacherName;
    private List<CourseDto> course;
}
