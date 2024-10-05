package com.example.DCRW.service.board;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.DCRW.dto.board.FileDto;
import com.example.DCRW.entity.File;
import com.example.DCRW.entity.Post;
import com.example.DCRW.repository.FileRepository;
import com.example.DCRW.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final AmazonS3Client amazonS3Client;
    private final FileRepository fileRepository;
    private final PostRepository postRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private String generateUniqueFileName(String originalFileName) {
        String uuid = UUID.randomUUID().toString();
        String extension = "";

        if (originalFileName != null) {
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = originalFileName.substring(dotIndex);
            }
        }

        return uuid + extension;
    }

    // 파일 업로드 및 File 엔티티 설정
    @Override
    @Transactional
    public List<File> uploadFiles(int postId, List<MultipartFile> files, String folder) throws IOException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 post 입력"));

        List<File> fileList = new ArrayList<>();
        List<String> uploadedS3Keys = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                String originalFileName = file.getOriginalFilename();
                String uniqueFileName = generateUniqueFileName(originalFileName);
                String s3Key = folder + "/" + uniqueFileName;
                String fileUrl = amazonS3Client.getUrl(bucket, s3Key).toString();

                // 파일 메타데이터 설정
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(file.getContentType());
                metadata.setContentLength(file.getSize());

                // S3에 파일 업로드
                amazonS3Client.putObject(bucket, s3Key, file.getInputStream(), metadata);
                uploadedS3Keys.add(s3Key); // 성공적으로 업로드된 S3 키 기록

                // File 엔티티 생성
                File fileEntity = File.builder()
                        .fileName(originalFileName)
                        .filePath(s3Key)
                        .fileUrl(fileUrl)
                        .fileType(file.getContentType())
                        .fileSize(file.getSize())
                        .uploadedAt(LocalDateTime.now())
                        .post(post)
                        .build();

                // File 엔티티 저장
                fileRepository.save(fileEntity);
                fileList.add(fileEntity);
            }

            // 트랜잭션이 롤백될 경우 업로드된 파일을 삭제하도록 동기화
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) {
                        // 트랜잭션이 롤백되면 업로드된 파일 삭제
                        for (String s3Key : uploadedS3Keys) {
                            try {
                                amazonS3Client.deleteObject(bucket, s3Key);
                                System.out.println("파일 삭제 성공 (롤백 시): " + s3Key);
                            } catch (Exception e) {
                                System.err.println("파일 삭제 실패 (롤백 시): " + s3Key + ". 오류 메시지: " + e.getMessage());
                            }
                        }
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("파일 업로드 중 입출력 오류가 발생했습니다: " + e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }

        return fileList;
    }


    // 파일 삭제
    @Override
    @Async
    @Retryable(value = IOException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void deleteFilesAfterCommit(List<File> fileEntities) {

        for (File fileEntity : fileEntities) {
            String s3Key = fileEntity.getFileUrl();
            try {
                amazonS3Client.deleteObject(bucket, s3Key);
                System.out.println("파일 삭제 성공: " + s3Key);
            } catch (AmazonServiceException e) {
                System.err.println("파일 삭제 실패: " + s3Key + ". AWS 오류 메시지: " + e.getMessage());
                throw e;  // 재시도 하도록 예외 다시 던짐
            } catch (SdkClientException e) {
                System.err.println("파일 삭제 실패: " + s3Key + ". 클라이언트 오류 메시지: " + e.getMessage());
                throw e;  // 재시도 하도록 예외 다시 던짐
            } catch (Exception e) {
                System.err.println("파일 삭제 실패: " + s3Key + ". 오류 메시지: " + e.getMessage());
                throw new RuntimeException("파일 삭제 중 알 수 없는 오류가 발생했습니다.", e);
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

    @Override
    public String encodeFileName(String fileName) {
        try {
            return URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported", e);
        }
    }


}
