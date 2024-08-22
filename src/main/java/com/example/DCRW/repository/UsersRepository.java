package com.example.DCRW.repository;

import com.example.DCRW.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // JpaRepository<엔티티, 엔티티의 id의 Reference Type>
public interface UsersRepository extends JpaRepository<Users, String> {
    Boolean existsByUserId(String username);

    Users findByUserId(String username);
}
