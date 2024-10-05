package com.example.DCRW.service.board;

import com.example.DCRW.dto.board.PostAddDto;
import com.example.DCRW.dto.board.PostDetailDto;
import com.example.DCRW.dto.board.PostResponseDto;
import com.example.DCRW.dto.board.PostUpdateDto;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardService {
    // 전체
    Page<PostResponseDto> showPost(int boardType, int page, int size);

    // 카테고리 검색
    Page<PostResponseDto> searchCategory(int boardType, int categoryId, int page, int size);

    // 검색어로 검색
    Page<PostResponseDto> searchQuery(int boardType, String query, int page, int size);

    // 카테고리, 검색어로 검색
    Page<PostResponseDto> searchByCategoryAndQuery(int boardType, int categoryId, String query, int page, int size);

    // 게시글 상세 조회
    PostDetailDto detailPost(int postId);

    // 게시글 등록
    @Transactional
    PostDetailDto addPost(String username, List<MultipartFile> files, PostAddDto postAddDto);

    // 게시글 삭제
    @Transactional
    void removePost(int postId);

    // 게시글 수정
    @Transactional
    PostDetailDto updatePost(int postId, PostUpdateDto postUpdateDto, List<MultipartFile> filesToAdd, String username);


}
