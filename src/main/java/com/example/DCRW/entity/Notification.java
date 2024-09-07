package com.example.DCRW.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    @Id
    @Column(name = "notification_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;
    private String content;
    @Column(name = "notification_date")
    private Timestamp notificationDate;
    @Column(name = "read_status")
    private int readStatus;  

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    @Builder
    public Notification(String content, Timestamp notificationDate, int readStatus) {
        this.content = content;
        this.notificationDate = notificationDate;
        this.readStatus = readStatus;
    }
}
