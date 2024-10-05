package com.example.DCRW.repository;

import com.example.DCRW.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query("SELECT c FROM Course c WHERE c.users.userId LIKE :username")
    List<Course> courseFind(@Param("username") String username);

    Optional<Course> findByCourseId(int courseId);

    @Query("select c from Course c where c.courseId =:courseId and c.users.userId like :userId")
    Course findByCourseIdAndTeacherId(@Param("courseId") int courseId, @Param("userId") String userId);
}
