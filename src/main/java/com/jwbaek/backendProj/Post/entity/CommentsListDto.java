package com.jwbaek.backendProj.Post.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class CommentsListDto {
    private Long id;
    private Long postId;
    private String author;
    private String content;
    private Long comParentId;
    private Long likeCnt;
    private LocalDateTime updatedDate;

    private List<CommentsListDto> reCommentList; // 대댓글 리스트

}
