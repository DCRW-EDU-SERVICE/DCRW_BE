package com.example.DCRW.dto.user;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StudentDto {
    private String studentId;
    private String studentName;

}
