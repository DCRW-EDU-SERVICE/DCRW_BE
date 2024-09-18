package com.example.DCRW.service.user;

import com.example.DCRW.dto.user.UserDto;
import com.example.DCRW.dto.user.UserUpdateDto;
import com.example.DCRW.entity.Users;
import com.example.DCRW.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{
    private final UsersRepository usersRepository;

    @Override
    public UserDto showProfile(String username){
        try {
            UserDto userDto = usersRepository.showProfile(username);

            if (userDto == null) {
                throw new NoSuchElementException("프로필 조회 사용자 정보 찾을 수 없음");
            }

            return userDto;
        } catch (Exception e) {
            // 예상치 못한 오류 발생 시
            throw new RuntimeException("프로필 조회에 오류가 발생했습니다", e);
        }
    }
    @Override
    @Transactional
    public Users updateProfile(String username, UserUpdateDto userUpdateDto) throws SQLException {
        try {
            Users users = usersRepository.findById(username)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

            userUpdateDto.getUserName().ifPresent(users::setName);
            userUpdateDto.getBirthDate().ifPresent(users::setBirthDate);
            userUpdateDto.getAddress().ifPresent(users::setAddress);
//        userUpdateDto.getImage().ifPresent(users::setImage);

            return usersRepository.save(users);
        } catch (ResponseStatusException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException("프로필 업데이트에 오류가 발생했습니다", e);
        }
    }

    @Override
    @Transactional
    public void deleteProfile(String username){
        try{
            usersRepository.deleteById(username);
        } catch (NoSuchElementException e){
            throw new NoSuchElementException();
        } catch (Exception e){
            throw new RuntimeException("프로필 삭제에 오류가 발생했습니다");
        }
    }
}
