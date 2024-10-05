package com.example.DCRW.dto.course;

import com.example.DCRW.dto.course.plan.CoursePlanDto;
import com.example.DCRW.entity.CoursePlan;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CourseUpdateDto {
    private Optional<String> title;
    private Optional<List<CoursePlan>> coursePlanList;
}
