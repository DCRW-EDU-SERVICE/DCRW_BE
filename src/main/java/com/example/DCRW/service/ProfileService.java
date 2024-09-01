package com.example.DCRW.service;

import com.example.DCRW.dto.UserDto;
import com.example.DCRW.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UsersRepository usersRepository;

    public UserDto showProfile(String username){
        try {
            UserDto userDto = usersRepository.showProfile(username);

            if (userDto == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보 찾을 수 없음");
            }

            return userDto;
        } catch (Exception e) {
            // 예상치 못한 오류 발생 시
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 오류가 발생했습니다", e);
        }
    }
}
