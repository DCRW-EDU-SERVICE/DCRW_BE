package com.example.DCRW.service.board.comments;

import com.example.DCRW.dto.board.comments.CommentAddDto;
import com.example.DCRW.dto.board.comments.CommentDto;
import com.example.DCRW.entity.Comments;
import com.example.DCRW.entity.Post;
import com.example.DCRW.entity.Users;
import com.example.DCRW.repository.CommentsRepository;
import com.example.DCRW.repository.PostRepository;
import com.example.DCRW.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;
    private final UsersRepository usersRepository;
    private final PostRepository postRepository;

    @Override
    public Comments addComments(int postId, CommentAddDto commentAddDto) {
        Users user = validateUser(commentAddDto.getUserId());
        Post post = validatePost(postId);

        Comments comments = Comments.builder()
                .users(user)
                .post(post)
                .content(commentAddDto.getContent())
                .commentDate(LocalDateTime.now())
                .build();

        return commentsRepository.save(comments);
    }

    @Override
    public Comments updateComments(int postId, int commentsId, String userId, CommentDto commentDto) {
        Comments comments = validateComment(commentsId);
        Users user = validateUser(userId);
        Post post = validatePost(postId);

        if(comments.getUsers() != user || comments.getPost() != post){
            throw new IllegalArgumentException("잘못된 입력");
        }

        comments.setContent(commentDto.getContent());

        return commentsRepository.save(comments);
    }

    private Users validateUser(String userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 ID"));
    }

    private Post validatePost(int postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 포스트 ID"));
    }

    private Comments validateComment(int commentsId) {
        return commentsRepository.findById(commentsId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 댓글 ID"));
    }
}
