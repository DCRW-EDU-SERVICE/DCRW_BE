package com.example.DCRW.entity;

import com.example.DCRW.dto.course.plan.CoursePlanDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Users users;

    private String title;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<CoursePlan> coursePlanList;

    @Builder
    public Course(Users users, String title, List<CoursePlan> coursePlanList) {
        this.users = users;
        this.title = title;
        this.coursePlanList = coursePlanList != null ? coursePlanList : new ArrayList<>();
    }

    public void setCoursePlanList(List<CoursePlan> coursePlanList) {
        this.coursePlanList = coursePlanList;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addCoursePlan(CoursePlan coursePlan) {
        // coursePlanList가 null이면 빈 리스트로 초기화
        if (this.coursePlanList == null) {
            this.coursePlanList = new ArrayList<>();
        }

        // coursePlanList에 coursePlan을 추가
        this.coursePlanList.add(coursePlan);

        // coursePlan의 course 필드를 현재 객체로 설정 (양방향 연관관계)
        coursePlan.setCourse(this);
    }

}
