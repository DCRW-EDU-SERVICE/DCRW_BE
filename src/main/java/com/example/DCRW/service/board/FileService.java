package com.example.DCRW.service.board;

import com.example.DCRW.entity.File;
import org.springframework.retry.annotation.Recover;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    List<File> uploadFiles(int postId, List<MultipartFile> files) throws IOException;
    void deleteFilesAfterCommit(List<File> fileEntities) throws IOException;

    @Recover
    void recover(IOException e, List<File> fileEntities);

    String encodeFileName(String fileName);
}
