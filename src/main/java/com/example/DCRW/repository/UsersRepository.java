package com.example.DCRW.repository;

import com.example.DCRW.dto.user.UserDto;
import com.example.DCRW.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository // JpaRepository<엔티티, 엔티티의 id의 Reference Type>
public interface UsersRepository extends JpaRepository<Users, String> {
    Boolean existsByUserId(String username);


    @Query("select new com.example.DCRW.dto.user.UserDto(u.name, u.birthDate, u.address, u.roleCode) from Users u where u.userId = :userId")
    UserDto showProfile(String userId);
}
