package com.example.DCRW.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "file")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;
    private String fileName;
    private String filePath;
    private int fileSize;
    private String fileType;
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
