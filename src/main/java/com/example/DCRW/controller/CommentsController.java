package com.example.DCRW.controller;

import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.board.comments.CommentAddDto;
import com.example.DCRW.dto.board.comments.CommentsResponseDto;
import com.example.DCRW.entity.Comments;
import com.example.DCRW.service.board.comments.CommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(name = "/boards")
@RequiredArgsConstructor
public class CommentsController {

    private final CommentsService commentsService;
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ResultDto<CommentsResponseDto>> addComments(@PathVariable("postId") int postId, @RequestBody CommentAddDto commentAddDto){
        Comments comments = commentsService.addComments(postId, commentAddDto);

        CommentsResponseDto commentsResponseDto = CommentsResponseDto.builder()
                .userName(comments.getUsers().getUserId())
                .content(comments.getContent())
                .commentDate(comments.getCommentDate())
                .build();

        ResultDto resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("댓글 등록 성공")
                .data(commentsResponseDto)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    @PatchMapping("/posts/{postId}/comments/{commentsId}")
    public ResponseEntity<ResultDto<String>> updateComments(){

    }

    @DeleteMapping("/posts/{postId}/comments/{commentsId}")
    public ResponseEntity<ResultDto<String>> deleteComments(){

    }
}
