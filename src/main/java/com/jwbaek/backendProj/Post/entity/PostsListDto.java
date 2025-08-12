package com.jwbaek.backendProj.Post.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostsListDto {

    private Long id;
    private String title;
    private String author;
    private LocalDateTime createdDate;
    private Long viewCnt;
    private Long orderId; // 순번 

    public PostsListDto(Long id, String title, String author, LocalDateTime createdDate,Long viewCnt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.createdDate = createdDate;
        this.viewCnt = viewCnt;
    }
}
