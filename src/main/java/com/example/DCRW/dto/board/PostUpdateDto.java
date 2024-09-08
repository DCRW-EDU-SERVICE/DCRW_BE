package com.example.DCRW.dto.board;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class PostUpdateDto {
    private Optional<String> title;
    private Optional<String> content;
    private List<Integer> fileDelete; // 삭제할 파일 ID 목록
    private List<MultipartFile> fileAdd; // 추가할 파일 목록
}
