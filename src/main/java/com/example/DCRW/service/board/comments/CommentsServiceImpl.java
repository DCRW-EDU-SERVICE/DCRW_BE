package com.example.DCRW.service.board.comments;


import com.example.DCRW.dto.board.comments.CommentAddDto;
import com.example.DCRW.dto.board.comments.CommentsResponseDto;
import com.example.DCRW.entity.Comments;
import com.example.DCRW.entity.Post;
import com.example.DCRW.entity.Users;
import com.example.DCRW.repository.CommentsRepository;
import com.example.DCRW.repository.PostRepository;
import com.example.DCRW.repository.UsersRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CommentsServiceImpl implements CommentsService{

    private final CommentsRepository commentsRepository;
    private final UsersRepository usersRepository;
    private final PostRepository postRepository;

    @Override
    public Comments addComments(int postId, CommentAddDto commentAddDto) {
        try {
            Users users = usersRepository.findByUserId(commentAddDto.getUserId());
            if (users == null) {
                throw new RuntimeException("잘못된 아이디");
            }

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 포스트"));

            Comments comments = Comments.builder()
                    .users(users)
                    .post(post)
                    .content(commentAddDto.getContent())
                    .commentDate(LocalDateTime.now())
                    .build();

            commentsRepository.save(comments);

            return comments;
        } catch (Exception e){
            throw new RuntimeException("댓글 등록 오류: " + e.getMessage());
        }
    }
}
