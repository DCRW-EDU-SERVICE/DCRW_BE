package com.example.DCRW.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer boardId;

    private String boardName;
}
