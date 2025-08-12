package com.jwbaek.backendProj.Post.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments,Long>{
    List<Comments> findByPostId(Long postId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Comments c WHERE c.comParentId = :comParentId") // 하위 댓글 삭제
    void deleteAllByComParentId(@Param("comParentId") Long comParentId);
}
