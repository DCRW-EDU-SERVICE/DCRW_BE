package com.example.DCRW.controller;

import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.board.PostPageDto;
import com.example.DCRW.dto.board.PostResponseDto;
import com.example.DCRW.service.board.BoardService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    // logger
//    private final Logger logger = LoggerFactory.getLogger(BoardController.class);

    // 전체, 독서, 한글, 부모 게시판 전체 조회
    @GetMapping
    public ResponseEntity<ResultDto<Object>> showBoards(@RequestParam("type") int boardType, @RequestParam("page") int page, @RequestParam("size") int size){
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
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "category", required = false) Integer category,
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
            postResponseDto = boardService.searchAll(boardType, page, size);
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
    @GetMapping("{boardType}/posts/{postID}")
    public ResponseEntity<ResultDto<Object>> detailPost(@PathVariable("boardType") int boardType, @PathVariable("postID") int postId){
        return null;
    }

    // 게시글 등록
    @PostMapping("{boardType}/posts")
    public ResponseEntity<ResultDto<Object>> addPosts(){
        return null;
    }

    // 게시글 수정
    @PatchMapping("{boardType}/posts/{postID}")
    public ResponseEntity<ResultDto<Object>> updatePost(){
        return null;
    }

    // 게시글 삭제
    @DeleteMapping("{boardType}/posts/{postID}")
    public ResponseEntity<ResultDto<Object>> deletePost(){
        return null;
    }
}
