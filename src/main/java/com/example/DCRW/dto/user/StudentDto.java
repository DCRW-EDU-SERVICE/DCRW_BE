package com.example.DCRW.dto.user;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StudentDto {
    private String studentId;
    private String studentName;
    private int age;

}
