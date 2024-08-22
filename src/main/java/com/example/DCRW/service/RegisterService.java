package com.example.DCRW.service;

import com.example.DCRW.dto.UserDto;
import com.example.DCRW.entity.Users;
import com.example.DCRW.repository.UsersRepository;
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
    public void Register(UserDto userDto){
        String userName = userDto.getUserId();
        String password = userDto.getPassword();

        Boolean isExist = userRepository.existsByUserId(userName);
        if(isExist){
            return;
        }

        Users data = Users.builder()
                .userId(userName)
                .password(bCryptPasswordEncoder.encode(password))
                .name(userDto.getName())
                .address(userDto.getAddress())
                .birthDate(userDto.getBirthDate())
                .build();

        userRepository.save(data);
    }
}
