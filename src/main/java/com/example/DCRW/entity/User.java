package com.example.DCRW.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jdk.jfr.Name;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Getter
@Setter
public class User {

    @Id
    private String userId;

    private String password;
    private String name;

    @Name(value = "birthdate")
    private Date birthDate;

    private String address;

    private int roleCode; // 관리자(0), 교사(1), 학생(2)
    private Timestamp joinDate;
}
