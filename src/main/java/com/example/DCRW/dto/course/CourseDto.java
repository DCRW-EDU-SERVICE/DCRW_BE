package com.example.DCRW.dto.course;

import com.example.DCRW.dto.user.StudentDto;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseDto {
    private int courseId;
    private String title;
    private List<StudentDto> student;
}
