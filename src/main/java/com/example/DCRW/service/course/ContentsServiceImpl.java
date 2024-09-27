package com.example.DCRW.service.course;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.DCRW.dto.course.ContentAddDto;
import com.example.DCRW.dto.course.ContentDto;
import com.example.DCRW.dto.course.ContentResponseDto;
import com.example.DCRW.dto.course.CourseFileDto;
import com.example.DCRW.entity.Course;
import com.example.DCRW.entity.CourseContent;
import com.example.DCRW.entity.Enrollment;
import com.example.DCRW.entity.Users;
import com.example.DCRW.repository.CourseContentRepository;
import com.example.DCRW.repository.CourseRepository;
import com.example.DCRW.repository.EnrollmentRepository;
import com.example.DCRW.repository.UsersRepository;
import com.example.DCRW.service.board.FileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
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

    @Override
    public ContentResponseDto showContents(ContentDto contentDto) {
        try {
            String studentId = contentDto.getStudentId();
            int courseId = contentDto.getCourseId();


            Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
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

        } catch (Exception e) {
            throw new RuntimeException("강의 콘텐츠 조회 실패 " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<CourseFileDto> addContents(String username, List<MultipartFile> files, ContentAddDto contentDto, String folder) throws IOException {
        Users users = usersRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자"));

        Course course = courseRepository.findByCourseIdAndTeacherId(contentDto.getCourseId(), users.getUserId());
        if (course == null) {
            throw new IllegalArgumentException("잘못된 입력");
        }

        List<CourseFileDto> contentList = new ArrayList<>();
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

                CourseContent courseContent = CourseContent.builder()
                        .week(contentDto.getContentWeek())
                        .fileName(originalFileName)
                        .filePath(s3Key)
                        .fileUrl(fileUrl)
                        .fileType(file.getContentType())
                        .fileSize(file.getSize())
                        .uploadDate(LocalDateTime.now())
                        .course(course)
                        .build();

                courseContentRepository.save(courseContent);

                CourseFileDto courseFileDto = CourseFileDto.builder()
                        .week(courseContent.getWeek())
                        .fileSize(courseContent.getFileSize())
                        .filePath(courseContent.getFilePath())
                        .fileName(courseContent.getFileName())
                        .fileType(courseContent.getFileType())
                        .fileUrl(courseContent.getFileUrl())
                        .build();

                contentList.add(courseFileDto);
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

        return contentList;
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
}
