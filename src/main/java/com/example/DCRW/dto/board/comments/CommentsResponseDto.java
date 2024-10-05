package com.example.DCRW.dto.board.comments;

import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentsResponseDto {
    private String content;
    private LocalDateTime commentDate;
    private String userName;
}
