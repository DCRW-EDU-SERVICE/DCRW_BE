package com.example.DCRW.dto.board;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostPageDto<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}
