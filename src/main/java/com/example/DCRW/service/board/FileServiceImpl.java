package com.example.DCRW.service.board;

import com.example.DCRW.entity.File;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileServiceImpl implements FileService{

    // post를 위한 파일 엔티티 리스트 세팅
    @Override
    public List<File> settingFile(List<MultipartFile> files){

        List<File> fileList = new ArrayList<>();

        for(MultipartFile file : files){
            String fileName = file.getOriginalFilename();
            String fileType = file.getContentType();

            Long fileSize = file.getSize();

            File fileEntity = File.builder()
                    .fileName(fileName)
                    .fileSize(fileSize)
                    .fileType(fileType)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            fileList.add(fileEntity);
        }
        return fileList;
    }

    // 파일 저장
    @Override
    public void saveFile(List<MultipartFile> files, List<File> fileList) {
        try {
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                File fileEntity = fileList.get(i);

                Path filePath = Path.of(fileEntity.getFilePath());

                Files.createDirectories(filePath.getParent()); // 디렉토리 존재하지 않으면 생성
                file.transferTo(filePath.toFile()); // file을 filePath에 저장
            }
        } catch (IOException ie) {
            ie.printStackTrace();
            throw new RuntimeException("파일 업로드 중 입출력 오류가 발생했습니다");
        } catch (Exception e){
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다 " + e.getMessage());
        }
    }

    // 파일 삭제
    @Override
    @Async
    @Retryable(value = IOException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void deleteFilesAfterCommit(List<File> fileEntities) throws IOException {
        for (File fileEntity : fileEntities) {
            Path filePath = Paths.get(fileEntity.getFilePath());
            try {
                Files.deleteIfExists(filePath);
                System.out.println("파일 삭제 성공: " + filePath);
            } catch (IOException e) {
                // 첫 시도에서 실패하면 3번까지 재시도
                System.err.println("파일 삭제 실패: " + filePath + ". 오류 메시지: " + e.getMessage());
                throw e;  // 재시도 하도록 예외 다시 던짐
            }
        }
    }

    @Override
    @Recover
    public void recover(IOException e, List<File> fileEntities) {
        // 모든 재시도가 실패한 경우 처리 로직
        System.err.println("파일 삭제 재시도 실패, 수동 처리 필요. " + e.getMessage());
        // 관리자 알림 등 추가 처리 로직
    }

    // 파일 + 문자 URL 인코딩 적용
    @Override
    public String encodeFileName(String fileName) {
        try {
            return URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported", e);
        }
    }
}
