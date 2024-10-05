package com.example.DCRW.service.user;

import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.user.RegisterDto;
import com.example.DCRW.dto.user.TeacherDto;
import com.example.DCRW.entity.Users;
import com.example.DCRW.repository.UsersRepository;
import org.hibernate.sql.exec.ExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterServiceImpl implements RegisterService{
    private final UsersRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public RegisterServiceImpl(UsersRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    @Transactional
    public ResultDto<String> register(RegisterDto registerDto){
        try {
            String userName = registerDto.getUserId();
            String password = registerDto.getPassword();

            boolean isExist = checkUsernameDuplication(userName);

            if (isExist) {
                throw new IllegalArgumentException("중복된 id");
            }

            Users data = Users.builder()
                    .userId(userName)
                    .password(bCryptPasswordEncoder.encode(password))
                    .name(registerDto.getName())
                    .address(registerDto.getAddress())
                    .birthDate(registerDto.getBirthDate())
                    .build();

            userRepository.save(data);

            return ResultDto.res(HttpStatus.OK, "회원가입 성공");
        } catch (Exception e){
            throw new RuntimeException("회원가입에 오류가 발생했습니다");
        }
    }

    // user id 중복 체크
    @Override
    @Transactional(readOnly = true)
    public boolean checkUsernameDuplication(String username){
        Boolean isExist = userRepository.existsByUserId(username);
        return isExist;
    }

    @Override
    @Transactional
    public void updateTeacher(TeacherDto teacherDto, String username) {
        try {
            String teacherId = teacherDto.getTeacherId();

            Users users = userRepository.findById(username)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

            users.setTeacherCode(teacherDto.getTeacherId());
            users.setRoleCode(1);

            userRepository.save(users);

        } catch (Exception e){
            throw new RuntimeException("교사 코드 등록 오류: " + e.getMessage());
        }
    }
}
