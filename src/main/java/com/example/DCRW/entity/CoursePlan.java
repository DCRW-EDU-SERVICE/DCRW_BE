package com.example.DCRW.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "course_plan")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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
}
