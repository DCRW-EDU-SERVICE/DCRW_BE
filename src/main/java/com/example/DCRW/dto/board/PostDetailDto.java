package com.example.DCRW.dto.board;

import com.example.DCRW.entity.File;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostDetailDto {
    private String title;
    private int category;
    private String content;
    private List<File> file;
}
