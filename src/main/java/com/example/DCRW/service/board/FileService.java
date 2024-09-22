package com.example.DCRW.service.board;

import com.example.DCRW.entity.File;
import org.springframework.retry.annotation.Recover;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    // 파일 업로드 및 File 엔티티 설정
    @Transactional
    List<File> uploadFiles(int postId, List<MultipartFile> files, String folder) throws IOException;

    void deleteFilesAfterCommit(List<File> fileEntities) throws IOException;

    @Recover
    void recover(IOException e, List<File> fileEntities);

    String encodeFileName(String fileName);
}
