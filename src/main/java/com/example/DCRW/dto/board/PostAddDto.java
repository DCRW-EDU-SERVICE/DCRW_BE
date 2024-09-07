package com.example.DCRW.dto.board;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Optional;

@Setter
@Getter
public class PostAddDto {
    private String title;
    private String content;
    private int category;
    private int boardId;
//    private LocalDate postDate = LocalDate.now();
}
