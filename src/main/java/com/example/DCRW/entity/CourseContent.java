package com.example.DCRW.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_content")
@Getter
public class CourseContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contentId;

    private int week;

    private String filePath;

    private String fileType;

    private LocalDateTime uploadDate;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;


}
