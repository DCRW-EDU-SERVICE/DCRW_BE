package com.example.DCRW.service.course;

import com.example.DCRW.dto.course.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ContentsService {

    // 교사 -> 강의 콘텐츠 조회
    ContentResponseDto showContentsTeacher(ContentDto contentDto, String username);

    // 학생 페이지 강의 콘텐츠 조회
    ContentResponseDto showContentsStudent(int courseId, String username);

    List<ContentFileResponseDto> addContents(String username, List<MultipartFile> files, ContentAddDto contentAddDto, String folder) throws IOException;

    void deleteContents(int contentId, String username);

}
