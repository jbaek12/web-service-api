package com.jwbaek.backendProj.Post.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor 
@Builder
public class PostsHis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Column(nullable = false)
    private Long postId; 

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, length = 10)
    private String author; // 게시글 작성자

    @Column(nullable = true, length = 10) 
    private String modUserId; // 게시글을 변경하거나 삭제한 사용자 

    @Column(nullable = false, length = 5)
    private String status; // 'C':등록, 'U':수정, 'D':삭제 - 이벤트 타입

    @CreationTimestamp 
    private LocalDateTime createdDate;

}