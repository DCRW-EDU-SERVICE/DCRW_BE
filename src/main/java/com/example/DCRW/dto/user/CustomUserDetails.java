package com.example.DCRW.dto.user;

import com.example.DCRW.entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final Users users;
    private final String role;

    public CustomUserDetails(Users users, String role) {
        this.users = users;
        this.role = role;
    }

    // role 값 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 역할을 GrantedAuthority로 변환
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    // password 값 반환
    @Override
    public String getPassword() {
        return users.getPassword();
    }

    // user
    @Override
    public String getUsername() {
        return users.getUserId();
    }

    // 계정이 Expired 됐는지
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    // 계정이 lock 됐는지
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
