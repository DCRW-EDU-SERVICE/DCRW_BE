package com.example.DCRW.service.board;

import com.example.DCRW.entity.File;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    // post를 위한 파일 엔티티 리스트 세팅
    List<File> settingFile(List<MultipartFile> files);

    // 파일 저장
    void saveFile(List<MultipartFile> files, List<File> fileList);

    // 파일 삭제
    @Async
    @Retryable(value = IOException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    void deleteFilesAfterCommit(List<File> fileEntities) throws IOException;

    @Recover
    void recover(IOException e, List<File> fileEntities);

    // 파일 + 문자 URL 인코딩 적용
    String encodeFileName(String fileName);
}
