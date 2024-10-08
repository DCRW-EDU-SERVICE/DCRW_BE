package com.example.DCRW.repository;

import com.example.DCRW.entity.CourseContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseContentRepository extends JpaRepository<CourseContent, Integer> {

    @Query("select c from CourseContent c where c.course.courseId =:courseId")
    List<CourseContent> findByCourseId(int courseId);

    @Query("select c from CourseContent c where c.course.courseId =:courseId and c.week =:week")
    CourseContent findByCourseIdAndWeekId(@Param("courseId")int courseId, @Param("week") int contentWeek);
}
