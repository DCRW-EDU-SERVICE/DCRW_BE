package com.example.DCRW.service.course;

import com.example.DCRW.dto.course.CourseAddDto;
import com.example.DCRW.dto.course.CourseDto;
import com.example.DCRW.dto.course.TeacherCourseDto;
import com.example.DCRW.dto.course.plan.CoursePlanDto;
import com.example.DCRW.dto.user.StudentDto;
import com.example.DCRW.entity.Course;
import com.example.DCRW.entity.CoursePlan;
import com.example.DCRW.entity.Users;
import com.example.DCRW.repository.CourseRepository;
import com.example.DCRW.repository.EnrollmentRepository;
import com.example.DCRW.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService{

    private final UsersRepository usersRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    // 사용자 찾기
    private Users findUsers(String username){
        Users users = usersRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return users;
    }

    // 선생님 강의 화면 - 추후 권한 설정 필요
    @Override
    public TeacherCourseDto teacherCourse(String username) {
        Users users = findUsers(username);

        try {
            List<Course> courseList = courseRepository.courseFind(username); // 해당 선생님의 강의 리스트

            List<CourseDto> courseDtoList = new ArrayList<>();
            for (Course course : courseList) {
                List<StudentDto> studentList = enrollmentRepository.studentFind(course.getCourseId());

                CourseDto courseDto = CourseDto.builder()
                        .title(course.getTitle())
                        .courseId(course.getCourseId())
                        .student(studentList)
                        .build();

                courseDtoList.add(courseDto);
            }

            TeacherCourseDto teacherCourseDto = TeacherCourseDto.builder()
                    .teacherName(users.getName())
                    .course(courseDtoList)
                    .build();

            return teacherCourseDto;

        } catch (Exception e){
            throw new RuntimeException("선생님 강의 관리 페이지 조회에 오류 발생 " + e.getMessage());
        }
    }

    // 강의 생성
    @Override
    public Map<String, String> addTeacherCourse(CourseAddDto courseAddDto, String username) {
        Users users = findUsers(username);

        Course course = Course.builder()
                .title(courseAddDto.getTitle())
                .users(users)
                .build();

        List<CoursePlan> coursePlanList = new ArrayList<>();
        for(CoursePlanDto coursePlanDto : courseAddDto.getCoursePlanList()){
            CoursePlan coursePlan = CoursePlan.builder()
                    .week(coursePlanDto.getWeek())
                    .content(coursePlanDto.getContent())
                    .course(course)
                    .build();

            coursePlanList.add(coursePlan); // 리스트에 추가
        }

        course.setCoursePlanList(coursePlanList);

        // 저장
        Course course1 = courseRepository.save(course);

        Map<String, String> map = new HashMap<>();
        map.put("courseId", course1.getCourseId().toString());
        map.put("title", course1.getTitle());

        return map;
    }
}
