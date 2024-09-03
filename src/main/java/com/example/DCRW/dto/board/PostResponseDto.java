package com.example.DCRW.dto.board;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // 게시글 반환
public class PostResponseDto {
    private String title;
    private String content;
    private LocalDateTime postDate;
    private String userName; // 유저 이름
    private int categoryId;
}

/*
boardId
0: 전체
1: 독서교육
2: 한글교육
3: 부모교육

categoryId
1: 공지사항
2: 오류신고
3: 자료공유.
4: 자유글
5: QnA

 */