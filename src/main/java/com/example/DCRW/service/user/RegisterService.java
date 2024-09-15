package com.example.DCRW.service.user;

import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.user.RegisterDto;
import com.example.DCRW.dto.user.TeacherDto;
import org.springframework.transaction.annotation.Transactional;

public interface RegisterService {
    @Transactional
    ResultDto<String> register(RegisterDto registerDto);

    @Transactional(readOnly = true)
    boolean checkUsernameDuplication(String username);

    void updateTeacher(TeacherDto teacherDto, String username);
}
