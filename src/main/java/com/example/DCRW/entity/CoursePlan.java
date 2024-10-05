package com.example.DCRW.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course_plan")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class CoursePlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer planId;

    private int week;

    private String content;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Builder
    public CoursePlan(int week, String content, Course course) {
        this.week = week;
        this.content = content;
        this.course = course;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
