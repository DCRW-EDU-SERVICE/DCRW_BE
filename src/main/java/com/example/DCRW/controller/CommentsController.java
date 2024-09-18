package com.example.DCRW.controller;

import com.example.DCRW.dto.ResultDto;
import com.example.DCRW.dto.board.comments.CommentAddDto;
import com.example.DCRW.dto.board.comments.CommentDto;
import com.example.DCRW.dto.board.comments.CommentsResponseDto;
import com.example.DCRW.dto.user.CustomUserDetails;
import com.example.DCRW.entity.Comments;
import com.example.DCRW.service.board.comments.CommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(name = "/boards")
@RequiredArgsConstructor
public class CommentsController {

    private final CommentsService commentsService;

    // 댓글 등록
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ResultDto<CommentsResponseDto>> addComments(@PathVariable("postId") int postId, @RequestBody CommentAddDto commentAddDto){
        Comments comments = commentsService.addComments(postId, commentAddDto);

        CommentsResponseDto commentsResponseDto = setCommentsResponseDto(comments);

        ResultDto resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("댓글 등록 성공")
                .data(commentsResponseDto)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    // 댓글 수정
    @PatchMapping("/posts/{postId}/comments/{commentsId}")
    public ResponseEntity<ResultDto<CommentsResponseDto>> updateComments(@PathVariable("postId") int postId, @PathVariable("commentsId") int commentsId, @RequestBody CommentDto commentDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Comments comments = commentsService.updateComments(postId, commentsId, customUserDetails.getUsername(),commentDto);

        CommentsResponseDto commentsResponseDto = setCommentsResponseDto(comments);

        ResultDto resultDto = ResultDto.builder()
                .status(HttpStatus.OK)
                .message("댓글 수정 성공")
                .data(commentsResponseDto)
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    // 댓글 삭제
    @DeleteMapping("/posts/{postId}/comments/{commentsId}")
    public ResponseEntity<ResultDto<String>> deleteComments(){

    }


    // commentsResponseDto 설정
    private CommentsResponseDto setCommentsResponseDto(Comments comments){
        CommentsResponseDto commentsResponseDto = CommentsResponseDto.builder()
                .userName(comments.getUsers().getUserId())
                .content(comments.getContent())
                .commentDate(comments.getCommentDate())
                .build();

        return commentsResponseDto;
    }
}
