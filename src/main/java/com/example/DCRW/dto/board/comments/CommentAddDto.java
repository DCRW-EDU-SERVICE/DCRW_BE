package com.example.DCRW.dto.board.comments;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentAddDto {
    private String userId;
    private String content;
}
