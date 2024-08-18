package com.example.DCRW.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@DynamicInsert // null 값이 있는 필드 insert에서 제외됨
@Getter
@Setter
public class User {

    @Id
    @Column(name = "userId")
    private String userId;

    private String password;
    private String name;

    @Column(name = "birthdate")
    private Date birthDate;

    private String address;

    @Column(nullable = true)
    private int roleCode; // 관리자(0), 교사(1), 학생(2)
    @Column(nullable = true)
    private Timestamp joinDate;
}
