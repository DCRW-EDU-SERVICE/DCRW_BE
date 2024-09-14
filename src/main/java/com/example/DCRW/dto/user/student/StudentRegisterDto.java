package com.example.DCRW.dto.user.student;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentRegisterDto {
    private String studentId;
    private int courseId;
}
