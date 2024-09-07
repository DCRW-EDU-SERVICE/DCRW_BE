package com.example.DCRW.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "course_plan")
public class CoursePlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer planId;

    private int week;

    private String content;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
