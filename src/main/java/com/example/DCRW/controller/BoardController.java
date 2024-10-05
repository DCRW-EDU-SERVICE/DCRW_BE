package com.example.DCRW.controller;

import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.board.*;
import com.example.DCRW.dto.user.CustomUserDetails;
import com.example.DCRW.entity.File;
import com.example.DCRW.entity.Post;
import com.example.DCRW.repository.BoardRepository;
import com.example.DCRW.repository.CategoryRepository;
import com.example.DCRW.repository.UsersRepository;
import com.example.DCRW.service.board.BoardService;
import com.example.DCRW.service.board.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final FileService fileService;

    // 전체, 독서, 한글, 부모 게시판 전체 조회
    @GetMapping
    public ResponseEntity<ResultDto<Object>> showBoards(@RequestParam("type") int boardType, @RequestParam("page") int page, @RequestParam("size") int size) {
        Page<PostResponseDto> postResponseDto = boardService.showPost(boardType, page, size);

        ResultDto<Object> resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("게시글 조회 성공")
                .data(postResponseDto)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    // 게시글 검색(카테고리, 검색어)
    @GetMapping("/search")
    public ResponseEntity<ResultDto<Object>> searchPosts(
            @RequestParam("type") int boardType,
            @RequestParam(value = "category", required = false) Integer category,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {

        Page<PostResponseDto> postResponseDto;

        if (query != null && !query.isEmpty()) {
            if (category != null) {
                // 카테고리와 검색어 둘 다 있는 경우
                postResponseDto = boardService.searchByCategoryAndQuery(boardType, category, query, page, size);
            } else {
                // 검색어만 있는 경우
                postResponseDto = boardService.searchQuery(boardType, query, page, size);
            }
        } else if (category != null) {
            // 카테고리만 있는 경우
            postResponseDto = boardService.searchCategory(boardType, category, page, size);
        } else {
            // 카테고리도 검색어도 없는 경우 (전체 검색)
            postResponseDto = boardService.showPost(boardType, page, size);
        }

        PostPageDto<PostResponseDto> result = new PostPageDto<>(
                postResponseDto.getContent(),
                postResponseDto.getTotalElements(),
                postResponseDto.getTotalPages(),
                postResponseDto.getNumber()
        );

        ResultDto<Object> resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("게시글 검색 성공")
                .data(result)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }


    // 게시글 세부 조회
    @GetMapping("/posts/{postID}")
    public ResponseEntity<ResultDto<Object>> detailPost(@PathVariable("postID") int postId) {
        PostDetailDto detailDtos = boardService.detailPost(postId);

        ResultDto<Object> resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("게시글 상세 조회 성공")
                .data(detailDtos)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    // 게시글 등록 -> content-type이 multipart/form-data
    @PostMapping("{boardType}/posts")
    public ResponseEntity<ResultDto<PostDetailDto>> addPosts(
            @RequestPart("post") PostAddDto postAddDto,
            @RequestPart(value = "file", required = false) List<MultipartFile> files) {


        CustomUserDetails customUserDetails = getUsername(SecurityContextHolder.getContext().getAuthentication());


        // 포스트 및 파일 업로드
        PostDetailDto savedPost = boardService.addPost(customUserDetails.getUsername(), files, postAddDto);

        ResultDto resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("게시글 등록 성공")
                .data(savedPost)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);

    }

    // 게시글 수정
    @PatchMapping("{boardType}/posts/{postID}")
    public ResponseEntity<ResultDto<PostDetailDto>> updatePost(
            @PathVariable("boardType") int boardType,
            @PathVariable("postID") int postId,
            @RequestPart("post")PostUpdateDto postUpdateDto,
            @RequestPart(value = "file", required = false) List<MultipartFile> fileAddList) {

        CustomUserDetails customUserDetails = getUsername(SecurityContextHolder.getContext().getAuthentication());
        PostDetailDto updatePost = boardService.updatePost(postId, postUpdateDto, fileAddList, customUserDetails.getUsername());

        ResultDto resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("게시글 수정 성공")
                .data(updatePost)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    // 게시글 삭제
    @DeleteMapping("{boardType}/posts/{postID}")
    public ResponseEntity<ResultDto<Object>> deletePost(@PathVariable("boardType") int boardType, @PathVariable("postID") int postId) {
        boardService.removePost(postId);

        ResultDto resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("게시글 삭제 성공")
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    // 세션 username 추출
    private CustomUserDetails getUsername(Authentication authentication){
        return (CustomUserDetails) authentication.getPrincipal();
    }
}
