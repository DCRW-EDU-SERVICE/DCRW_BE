package com.example.DCRW.dto.course;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentFileResponseDto {
    private int contentId;
    private int week;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileUrl;
    private String fileType;

}
