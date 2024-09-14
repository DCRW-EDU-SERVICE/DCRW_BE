package com.example.DCRW.service.user.student;

import com.example.DCRW.dto.user.student.StudentRegisterDto;
import com.example.DCRW.dto.user.student.StudentSearchDto;

public interface StudentManageService {

    String searchStudentList(StudentSearchDto username);

    void studentRegister(String username, StudentRegisterDto studentRegisterDto);
}
