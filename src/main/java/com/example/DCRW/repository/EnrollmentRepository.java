package com.example.DCRW.repository;

import com.example.DCRW.dto.user.StudentDto;
import com.example.DCRW.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    @Query("select new com.example.DCRW.dto.user.StudentDto(u.userId, u.name) from Enrollment e join e.users u where e.course.courseId = :courseId")
    List<StudentDto> studentFind(@Param("courseId") Integer courseId);

    @Query("select e from Enrollment e where e.users.userId =:studentId and e.course.courseId =:courseId")
    Enrollment findByStudentIdAndCourseId(@Param("studentId") String studentId, @Param("courseId") Integer courseId);
}
