package com.example.DCRW.service;

import com.example.DCRW.dto.UserDto;
import com.example.DCRW.entity.User;
import com.example.DCRW.repository.UserRepository;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class RegisterService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public RegisterService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void Register(UserDto userDto){
        String userName = userDto.getUserId();
        String password = userDto.getPassword();

        Boolean isExist = userRepository.existsByUserId(userName);
        if(isExist){
            return;
        }

        User data = new User();
        data.setUserId(userName);
        data.setPassword(bCryptPasswordEncoder.encode(password)); // 비밀번호 암호화

        data.setName(userDto.getName());
        data.setAddress(userDto.getAddress());

        data.setBirthDate(userDto.getBirthDate());
//        data.setJoinDate((Timestamp) userDto.getJoinDate());

        userRepository.save(data);
    }
}
