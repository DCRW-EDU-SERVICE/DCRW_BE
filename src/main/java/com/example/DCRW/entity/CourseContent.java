package com.example.DCRW.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_content")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class CourseContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contentId;

    private int week;

    private String fileName;

    private String filePath;

    private String fileUrl;

    private String fileType;

    private Long fileSize;

    private LocalDateTime uploadDate;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

}
