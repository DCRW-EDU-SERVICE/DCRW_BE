package com.example.DCRW.service.course;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.DCRW.dto.course.*;
import com.example.DCRW.entity.*;
import com.example.DCRW.repository.CourseContentRepository;
import com.example.DCRW.repository.CourseRepository;
import com.example.DCRW.repository.EnrollmentRepository;
import com.example.DCRW.repository.UsersRepository;
import com.example.DCRW.service.board.FileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContentsServiceImpl implements ContentsService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final CourseContentRepository courseContentRepository;
    private final UsersRepository usersRepository;
    private final FileService fileService;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 교사 -> 강의 콘텐츠 조회
    @Override
    public ContentResponseDto showContentsTeacher(ContentDto contentDto, String username) {
        String studentId = contentDto.getStudentId();
        int courseId = contentDto.getCourseId();

        Course course = courseRepository.findByCourseIdAndTeacherId(courseId, username);
        if(course == null){
            throw new IllegalArgumentException("해당 교사의 강의를 찾을 수 없습니다");
        }

        return showContents(courseId, studentId);
    }

    // 학생 페이지 강의 콘텐츠 조회
    @Override
    public ContentResponseDto showContentsStudent(int courseId, String username) {
        return showContents(courseId, username);
    }

    private ContentResponseDto showContents(int courseId, String username){
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(username, courseId);
        if (enrollment == null) {
            throw new IllegalArgumentException("강의를 수강 중인 학생이 아닙니다");
        }

        List<CourseContent> courseContent = courseContentRepository.findByCourseId(courseId);
        List<CourseFileDto> fileDto = findFile(courseContent);

        ContentResponseDto contentResponseDto = ContentResponseDto.builder()
                .progressRate(enrollment.getProgressRate())
                .contentList(courseContent)
                .fileList(fileDto)
                .build();

        return contentResponseDto;
    }

    @Override
    @Transactional
    public List<ContentFileResponseDto> addContents(String username, List<MultipartFile> files, ContentAddDto contentDto, String folder) throws IOException {
        Users users = usersRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자"));

        validationContentRequest(username, contentDto.getCourseId());

        // 강의 아이디, 교사 아이디 검색해서 교사 본인의 강의가 아닐 경우 예외
        Course course = courseRepository.findByCourseIdAndTeacherId(contentDto.getCourseId(), users.getUserId());
        if (course == null) {
            throw new IllegalArgumentException("잘못된 입력");
        }

        // 강의 콘텐츠가 이미 있으면 수정, 없으면 새로 등록
        CourseContent existContent = courseContentRepository.findByCourseIdAndWeekId(contentDto.getCourseId(), contentDto.getContentWeek());

        List<ContentFileResponseDto> contentList = new ArrayList<>();
        List<String> uploadedS3Keys = new ArrayList<>();

        // S3에 파일 업로드 및 DB에 콘텐츠 등록
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

            CourseContent courseContent;
            // 등록(기존 강의 주차 콘텐츠 데이터 없음)
            if (existContent == null) {
                courseContent = CourseContent.builder()
                        .week(contentDto.getContentWeek())
                        .fileName(originalFileName)
                        .filePath(s3Key)
                        .fileUrl(fileUrl)
                        .fileType(file.getContentType())
                        .fileSize(file.getSize())
                        .uploadDate(LocalDateTime.now())
                        .course(course)
                        .build();
            } else{ // 수정(기존 데이터 있음)
                courseContent = CourseContent.builder()
                        .contentId(existContent.getContentId())
                        .week(existContent.getWeek())
                        .fileName(originalFileName)
                        .filePath(s3Key)
                        .fileUrl(fileUrl)
                        .fileType(file.getContentType())
                        .fileSize(file.getSize())
                        .uploadDate(LocalDateTime.now())
                        .course(course)
                        .build();
            }

            CourseContent result = courseContentRepository.save(courseContent);

            // 응답을 위해 강의콘텐츠 파일 dto
            ContentFileResponseDto responseDto = ContentFileResponseDto.builder()
                    .contentId(result.getContentId())
                    .week(result.getWeek())
                    .fileSize(result.getFileSize())
                    .filePath(result.getFilePath())
                    .fileName(result.getFileName())
                    .fileType(result.getFileType())
                    .fileUrl(result.getFileUrl())
                    .build();

            contentList.add(responseDto);
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

        return contentList;
    }

    @Override
    @Transactional
    public void deleteContents(int contentId, String username) {
        CourseContent courseContent = courseContentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 입력"));

        validationContentRequest(username, courseContent.getCourse().getCourseId());

        // db에서 삭제
        courseContentRepository.delete(courseContent);

        // 트랜잭션 성공 후 파일을 비동기로 삭제
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                deleteContentAfterCommit(courseContent);  // 비동기 파일 삭제 처리
            }
        });
    }



    private void validationContentRequest(String username, int courseId){
        Users users = usersRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자"));

        // 강의 아이디, 교사 아이디 검색해서 교사 본인의 강의가 아닐 경우 예외
        Course course = courseRepository.findByCourseIdAndTeacherId(courseId, users.getUserId());
        if (course == null) {
            throw new IllegalArgumentException("잘못된 입력");
        }
    }

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

    private List<CourseFileDto> findFile(List<CourseContent> contentList){
        List<CourseFileDto> fileList = new ArrayList<>();
        for(CourseContent content : contentList){
            CourseFileDto fileDto = CourseFileDto.builder()
                    .week(content.getWeek())
                    .fileName(content.getFileName())
                    .filePath(content.getFilePath())
                    .fileSize(content.getFileSize())
                    .fileUrl(content.getFileUrl())
                    .fileType(content.getFileType())
                    .build();

            fileList.add(fileDto);
        }
        return fileList;
    }

    // 강의콘텐츠 하나의 파일 삭제
    @Async
    @Retryable(value = IOException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void deleteContentAfterCommit(CourseContent courseContent) {
        String s3Key = courseContent.getFileUrl();

        try{
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
