package com.example.DCRW.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int postId;

    private String title;
    private String content;
    private LocalDateTime postDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // 댓글
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comments> commentsList = new ArrayList<>();

    // 파일
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<File> fileList = new ArrayList<>();

    @Builder
    public Post(String title, String content, LocalDateTime postDate, Users users, Board board, Category category, List<File> fileList) {
        this.title = title;
        this.content = content;
        this.postDate = postDate;
        this.users = users;
        this.board = board;
        this.category = category;
        this.fileList = fileList;
    }
}
