package com.example.DCRW.service.course;

import com.example.DCRW.dto.course.ContentDto;
import com.example.DCRW.dto.course.ContentResponseDto;

public interface ContentsService {
    ContentResponseDto showContents(ContentDto contentDto);
}
