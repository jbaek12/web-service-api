package com.jwbaek.backendProj.Post.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

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
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId; // 게시글 ID

    @Column(nullable= false, length = 10)
    private String author;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private Long comParentId; // 댓글 상위 ID

    @Column(nullable = false) 
    private Long likeCnt; // 댓글 좋아요 수

    @Column(nullable = false)
    private boolean isDeleted; // 삭제 여부

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;
 
    // 초기값 세팅
    @PrePersist
    public void prePersist() {
        this.likeCnt = this.likeCnt == null ? 0L : this.likeCnt;
        this.comParentId = this.comParentId == null ? 0L : this.comParentId; 
    }


}