package com.example.DCRW.service.user;

import com.example.DCRW.dto.user.UserDto;
import com.example.DCRW.dto.user.UserUpdateDto;
import com.example.DCRW.entity.Users;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

public interface ProfileService {
    UserDto showProfile(String username);

    @Transactional
    Users updateProfile(String username, UserUpdateDto userUpdateDto) throws SQLException;

    @Transactional
    void deleteProfile(String username);
}
