package com.example.DCRW.dto.course;

import com.example.DCRW.entity.CourseContent;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ContentResponseDto {
    private double progressRate;

    private List<CourseContent> contentList;
    private List<CourseFileDto> fileList; // 실제 파일

}
