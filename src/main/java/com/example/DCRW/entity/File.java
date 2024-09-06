package com.example.DCRW.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "file")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public void setPost(Post post) {
        this.post = post;
    }

    @Builder
    public File(String fileName, Long fileSize, String fileType, String filePath, LocalDateTime uploadedAt) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.filePath = filePath;
        this.uploadedAt = uploadedAt;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
