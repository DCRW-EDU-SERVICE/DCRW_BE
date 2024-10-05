package com.example.DCRW.repository;

import com.example.DCRW.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface CommentsRepository extends JpaRepository<Comments, Integer> {
}
