package com.example.DCRW.service.user.student;

import com.example.DCRW.dto.user.student.StudentCourseDto;
import com.example.DCRW.dto.user.student.StudentSearchDto;
import com.example.DCRW.entity.Course;
import com.example.DCRW.entity.Enrollment;
import com.example.DCRW.entity.Users;
import com.example.DCRW.repository.CourseRepository;
import com.example.DCRW.repository.EnrollmentRepository;
import com.example.DCRW.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentManageServiceImpl implements StudentManageService{

    private final UsersRepository usersRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    @Override
    public String searchStudentList(StudentSearchDto studentSearchDto) {
        Users users = usersRepository.findByUserId(studentSearchDto.getStudentId());

        // 학생이 아니면
        if(users.getRoleCode() != 2){
            throw new IllegalArgumentException("잘못된 학생 요청");
        }
        return users.getName();
    }

    @Override
    @Transactional
    public void studentRegister(String username, StudentCourseDto studentCourseDto) {
        Course course = courseRepository.findByCourseIdAndTeacherId(studentCourseDto.getCourseId(), username);
        if(course == null){ // 강의가 존재하지 않으면
            throw new IllegalArgumentException("강의 잘못된 요청");
        }

        Users users = usersRepository.findByUserId(studentCourseDto.getStudentId());
        if(users == null || users.getRoleCode() != 2){ // 학생 user가 존재하지 않거나, 존재하지만 role이 학생이 아니거나
            throw new IllegalArgumentException("학생 잘못된 요청");
        }

        Enrollment enrollResult = enrollmentRepository.findByStudentIdAndCourseId(studentCourseDto.getStudentId(), studentCourseDto.getCourseId());
        if(enrollResult != null){ // 이미 수강중이면
            throw new IllegalArgumentException("현재 해당 강의를 듣고 있는 학생입니다");
        }

        try{
            Enrollment enrollment = Enrollment.builder()
                    .users(users)
                    .course(course)
                    .build();

            enrollmentRepository.save(enrollment);

        } catch (Exception e){
            throw new RuntimeException("학생 강의 등록 오류: " + e.getMessage());
        }

    }

    @Override
    @Transactional
    public void studentDelete(String username, StudentCourseDto studentCourseDto) {
        // 내 강의가 맞는지 확인(교사)
        Course course = courseRepository.findByCourseIdAndTeacherId(studentCourseDto.getCourseId(), username);
        if(course == null){
            throw new IllegalArgumentException("강의 잘못된 요청");
        }

        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentCourseDto.getStudentId(), studentCourseDto.getCourseId());
        if(enrollment == null){
            throw new IllegalArgumentException("해당 강의를 수강하고 있지 않습니다");
        }

        try{
             enrollmentRepository.delete(enrollment);
        } catch (Exception e){
            throw new RuntimeException("학생 강의 삭제 오류: " + e.getMessage());
        }
    }
}
