package com.example.DCRW.service.course;

import com.example.DCRW.dto.course.CourseAddDto;
import com.example.DCRW.dto.course.CourseDto;
import com.example.DCRW.dto.course.CourseUpdateDto;
import com.example.DCRW.dto.course.TeacherCourseDto;
import com.example.DCRW.dto.course.plan.CoursePlanDto;
import com.example.DCRW.dto.user.StudentDto;
import com.example.DCRW.entity.Course;
import com.example.DCRW.entity.CoursePlan;
import com.example.DCRW.entity.Users;
import com.example.DCRW.repository.CourseRepository;
import com.example.DCRW.repository.EnrollmentRepository;
import com.example.DCRW.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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

    // 강의 수정, 삭제할 권한이 있는지 찾기
    private Course findAuthority(int courseId, String username){
        // 강의 찾기
        Course course = courseRepository.findByCourseId(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강의가 없습니다"));


        if (course.getUsers() == null) {
            throw new IllegalStateException("강의 등록자를 찾을 수 없습니다.");
        }

        // 요청한 유저의 사용자 이름과 강의의 유저 비교
        if (!course.getUsers().getUserId().equals(username)) {

            throw new SecurityException("사용자가 강의를 수정할 수 있는 권한이 없습니다");
        }

        return course;
    }

    // 선생님 강의 화면 - 추후 권한 설정 필요
    @Override
    public TeacherCourseDto teacherCourse(String username) {
        Users users = findUsers(username);

        try {
            List<Course> courseList = courseRepository.courseFind(username); // 해당 선생님의 강의 리스트

            List<CourseDto> courseDtoList = new ArrayList<>();
            for (Course course : courseList) {
                List<Users> studentListTemp = enrollmentRepository.studentFind(course.getCourseId());

                List<StudentDto> studentList = new ArrayList<>();

                for(Users student : studentListTemp){
                    StudentDto studentDto = StudentDto.builder()
                            .studentId(student.getUserId())
                            .studentName(student.getName())
                            .age(birthdateToAge(student.getBirthDate()))
                            .build();

                    studentList.add(studentDto);
                }

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
            throw new RuntimeException("학생,강의 조회에 오류 발생 " + e.getMessage());
        }
    }


    private int birthdateToAge(LocalDate birthDate) {
        LocalDate nowDate = LocalDate.now();

        int age = nowDate.getYear() - birthDate.getYear();

        // 생일 안지났으면 나이를 하나 줄인다.
        if (nowDate.getMonthValue() * 100 + nowDate.getDayOfMonth() <
                birthDate.getMonthValue() * 100 + birthDate.getDayOfMonth()) {
            age--;
        }

        return age;
    }


    // 강의 생성
    @Override
    @Transactional
    public Map<String, String> addTeacherCourse(CourseAddDto courseAddDto, String username) {
        try {
            Users users = findUsers(username);

            Course course = Course.builder()
                    .title(courseAddDto.getTitle())
                    .users(users)
                    .build();

            List<CoursePlan> coursePlanList = new ArrayList<>();
            for (CoursePlanDto coursePlanDto : courseAddDto.getCoursePlanList()) {
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
        } catch (Exception e){
            throw new RuntimeException("강의 생성 오류 " + e.getMessage());
        }
    }

    // 강의 수정
    @Override
    @Transactional
    public Course updateCourse(int courseId, CourseUpdateDto courseUpdateDto, String username) {
        Course course = findAuthority(courseId, username);

        try {
            // 강의 제목이 있을 경우 수정
            courseUpdateDto.getTitle().ifPresent(course::setTitle);

            // 강의 계획서가 있을 경우 수정
            if (courseUpdateDto.getCoursePlanList().isPresent()) {
                List<CoursePlan> newPlans = courseUpdateDto.getCoursePlanList().get();

                // 기존 계획서 가져오기
                List<CoursePlan> existingPlans = course.getCoursePlanList();

                for (CoursePlan newPlan : newPlans) {
                    // 기존 계획서에서 주차별로 매칭되는 계획서 찾기
                    Optional<CoursePlan> existingPlanOpt = existingPlans.stream()
                            .filter(plan -> plan.getWeek() == newPlan.getWeek())
                            .findFirst();

                    if (existingPlanOpt.isPresent()) {
                        // 기존 계획서가 있으면 수정
                        CoursePlan existingPlan = existingPlanOpt.get();
                        existingPlan.setContent(newPlan.getContent());
                    } else {
                        // 기존 계획서가 없으면 새로 추가
                        CoursePlan newCoursePlan = CoursePlan.builder()
                                .week(newPlan.getWeek())
                                .content(newPlan.getContent())
                                .course(course)
                                .build();
                        course.addCoursePlan(newCoursePlan);
                    }
                }
            }

            // 저장
            Course updatedCourse = courseRepository.save(course);

            return updatedCourse;

        } catch (Exception e){
            throw new RuntimeException("강의 수정에 오류 발생 " + e.getMessage());
        }
    }

    // 강의 삭제
    @Override
    @Transactional
    public void deleteCourse(int courseId, String username) {
        Course course = findAuthority(courseId, username);

        try{
            courseRepository.delete(course);
        } catch (Exception e){
            throw new RuntimeException("강의 삭제에 오류 발생 " + e.getMessage());
        }
    }

}
