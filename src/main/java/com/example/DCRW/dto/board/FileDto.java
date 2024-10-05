package com.example.DCRW.dto.board;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FileDto {
    private Long fileId;
    private String fileName;
    private String filePath;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
}