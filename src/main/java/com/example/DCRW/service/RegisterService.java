package com.example.DCRW.service;

import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.UserDto;
import com.example.DCRW.entity.Users;
import com.example.DCRW.repository.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterService {
    private final UsersRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public RegisterService(UsersRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public ResultDto<String> register(UserDto userDto){
        String userName = userDto.getUserId();
        String password = userDto.getPassword();

        boolean isExist = checkUsernameDuplication(userName);

        if(isExist){
            return ResultDto.res(HttpStatus.BAD_REQUEST, "중복된 id");
        }

        Users data = Users.builder()
                .userId(userName)
                .password(bCryptPasswordEncoder.encode(password))
                .name(userDto.getName())
                .address(userDto.getAddress())
                .birthDate(userDto.getBirthDate())
                .build();

        userRepository.save(data);

        return ResultDto.res(HttpStatus.OK, "회원가입 성공");
    }

    @Transactional(readOnly = true)
    public boolean checkUsernameDuplication(String username){
        Boolean isExist = userRepository.existsByUserId(username);
        return isExist;
    }
}
