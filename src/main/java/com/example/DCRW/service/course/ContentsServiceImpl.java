package com.example.DCRW.service.course;

import com.example.DCRW.dto.board.FileDto;
import com.example.DCRW.dto.course.ContentDto;
import com.example.DCRW.dto.course.ContentResponseDto;
import com.example.DCRW.dto.course.CourseFileDto;
import com.example.DCRW.entity.CourseContent;
import com.example.DCRW.entity.Enrollment;
import com.example.DCRW.repository.CourseContentRepository;
import com.example.DCRW.repository.EnrollmentRepository;
import com.example.DCRW.service.board.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentsServiceImpl implements ContentsService{

    private final EnrollmentRepository enrollmentRepository;
    private final CourseContentRepository courseContentRepository;
    private final FileService fileService;


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

        } catch (Exception e){
            throw new RuntimeException("강의 콘텐츠 조회 실패 " + e.getMessage());
        }
    }

    private List<CourseFileDto> findFile(List<CourseContent> contentList){
        List<CourseFileDto> fileList = new ArrayList<>();
        for(CourseContent content : contentList){
            CourseFileDto fileDto = CourseFileDto.builder()
                    .week(content.getWeek())
                    .fileUrl("http://localhost:8080/" + content.getFilePath())
                    .fileType(content.getFileType())
                    .build();

            fileList.add(fileDto);
        }
        return fileList;
    }
}
