package com.example.DCRW.service.user;

import com.example.DCRW.dto.user.CustomUserDetails;
import com.example.DCRW.entity.Users;
import com.example.DCRW.repository.UsersRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UsersRepository usersRepository;

    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users users = usersRepository.findByUserId(username);

        // 권한 매핑
        if(users != null){
            String role;
            if (users.getRoleCode() == 1) {
                role = "ROLE_TEACHER"; // role이 1일 경우 ROLE_TEACHER
            } else if (users.getRoleCode() == 0) {
                role = "ROLE_ADMIN"; // role이 0일 경우 ROLE_ADMIN
            } else {
                role = "ROLE_USER"; // 나머지 ROLE_USER
            }

            return new CustomUserDetails(users, role);
        }



        throw new NoSuchElementException("사용자를 찾을 수 없습니다");
    }
}
