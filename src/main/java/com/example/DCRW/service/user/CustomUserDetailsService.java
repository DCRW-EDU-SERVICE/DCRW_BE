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
        if(users != null){
            return new CustomUserDetails(users);
        }

        throw new NoSuchElementException("사용자를 찾을 수 없습니다");
    }
}
