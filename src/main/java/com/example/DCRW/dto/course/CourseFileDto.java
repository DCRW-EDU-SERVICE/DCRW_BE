package com.example.DCRW.dto.course;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class CourseFileDto {
    private int week;
    private String fileUrl;
    private String fileType;

    @Builder
    public CourseFileDto(int week, String fileName, String fileUrl, String fileType, int fileSize) {
        this.week = week;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
    }
}
