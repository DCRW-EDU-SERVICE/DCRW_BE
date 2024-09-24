package com.example.DCRW.service.course;

import com.example.DCRW.dto.course.ContentAddDto;
import com.example.DCRW.dto.course.ContentDto;
import com.example.DCRW.dto.course.ContentResponseDto;
import com.example.DCRW.dto.course.CourseFileDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ContentsService {
    ContentResponseDto showContents(ContentDto contentDto);

    List<CourseFileDto> addContents(String username, List<MultipartFile> files, ContentAddDto contentAddDto, String folder) throws IOException;
}
