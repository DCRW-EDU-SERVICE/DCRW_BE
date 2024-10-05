package com.example.DCRW.dto.course.plan;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CoursePlanDto {
    private int week;
    private String content;
}
