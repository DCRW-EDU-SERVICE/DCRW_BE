package com.example.DCRW.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "course")
@Getter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Users users;

    private String title;
}
