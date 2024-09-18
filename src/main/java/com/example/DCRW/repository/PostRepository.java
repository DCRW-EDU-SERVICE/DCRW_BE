package com.example.DCRW.repository;

import com.example.DCRW.dto.board.PostResponseDto;
import com.example.DCRW.entity.Board;
import com.example.DCRW.entity.Category;
import com.example.DCRW.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Integer> {

    // 게시글 전체 조회 (페이지 별 / 전체 게시판, 독서 게시판, 한글 게시판, 교육 게시판)
    @Query("select new com.example.DCRW.dto.board.PostResponseDto(p.title, p.content, p.postDate, p.users.name, p.category.categoryId) " +
            "from Post p " +
            "join p.users " +
            "join p.category " +
            "where p.board = :board")
    Page<PostResponseDto> showPost(@Param("board") Board board, Pageable pageable);


    // 게시판 유형 별 카테고리 검색
    @Query("select new com.example.DCRW.dto.board.PostResponseDto(p.title, p.content, p.postDate, p.users.name, p.category.categoryId) " +
            "from Post p " +
            "join p.users " +
            "join p.category " +
            "where p.board = :board and p.category = :category")
    Page<PostResponseDto> searchPostCategory(@Param("board") Board board, Pageable pageable, @Param("category") Category category);

    @Query("select new com.example.DCRW.dto.board.PostResponseDto(p.title, p.content, p.postDate, p.users.name, p.category.categoryId) " +
            "from Post p " +
            "join p.users " +
            "join p.category " +
            "where p.board = :board and p.title like :query")
    Page<PostResponseDto> searchPostTitle(@Param("board") Board board, Pageable pageable, @Param("query") String query);

    @Query("select new com.example.DCRW.dto.board.PostResponseDto(p.title, p.content, p.postDate, p.users.name, p.category.categoryId) " +
            "from Post p " +
            "join p.users " +
            "join p.category " +
            "where p.board = :board and p.title like :query and p.category = :category")
    Page<PostResponseDto> searchPostTitleCategory(Board board, Pageable pageable, Category category, String query);
}
