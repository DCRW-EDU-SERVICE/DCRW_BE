package com.example.DCRW.entity;

import jakarta.persistence.*;
import lombok.*;

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
        this.coursePlanList = coursePlanList;
    }

    public void setCoursePlanList(List<CoursePlan> coursePlanList) {
        this.coursePlanList = coursePlanList;
    }
}
