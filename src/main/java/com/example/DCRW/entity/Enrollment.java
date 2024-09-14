package com.example.DCRW.entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "enrollment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer enrollmentId;

    private float progressRate;
    private LocalDate completionDate;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Users users;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Builder
    public Enrollment(Users users, Course course) {
        this.users = users;
        this.course = course;
    }
}
