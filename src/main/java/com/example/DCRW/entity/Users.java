package com.example.DCRW.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@DynamicInsert // null 값이 있는 필드 insert에서 제외
@Getter
@Setter
public class Users {

    @Id
    @Column(name="user_id")
    private String userId;

    private String password;
    private String name;

    @Column(name = "birthdate")
    private LocalDate birthDate;

    private String address;

    @Column(name = "role_code", nullable = true)
    private int roleCode = 2; // 관리자(0), 교사(1), 학생(2)

    @Column(name = "join_date", nullable = true, updatable = false)
    private Timestamp joinDate;

    @Column(name = "teacher_code", nullable = true)
    private String teacherCode;

    @OneToMany(mappedBy = "users")
    private List<Notification> notification;

    public Users(){}
    @Builder
    public Users(String userId, String password, String name, LocalDate birthDate, String address) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.address = address;
    }
}
