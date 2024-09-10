package com.example.DCRW.dto.course;

import com.example.DCRW.dto.course.plan.CoursePlanDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CourseAddDto {
    private String title;
    private List<CoursePlanDto> coursePlanList;
}
