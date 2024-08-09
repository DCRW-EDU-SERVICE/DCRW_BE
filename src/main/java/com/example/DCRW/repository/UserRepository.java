package com.example.DCRW.repository;

import com.example.DCRW.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // JpaRepository<엔티티, 엔티티의 id의 Reference Type>
public interface UserRepository extends JpaRepository<User, String> {

}
