package com.example.DCRW.service.board.comments;

import com.example.DCRW.dto.board.comments.CommentAddDto;
import com.example.DCRW.entity.Comments;
import org.springframework.stereotype.Service;

@Service
public interface CommentsService {
    Comments addComments(int postId, CommentAddDto commentAddDto);
}
