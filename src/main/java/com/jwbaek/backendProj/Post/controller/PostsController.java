package com.jwbaek.backendProj.Post.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.Authentication;

import com.jwbaek.backendProj.Post.entity.CommentsListDto;
import com.jwbaek.backendProj.Post.entity.CommentsSaveDto;
import com.jwbaek.backendProj.Post.entity.CommentsUpdateDto;
import com.jwbaek.backendProj.Post.entity.Posts;
import com.jwbaek.backendProj.Post.entity.PostsListDto;
import com.jwbaek.backendProj.Post.entity.PostsSaveDto;
import com.jwbaek.backendProj.Post.entity.PostsUpdateDto;
import com.jwbaek.backendProj.Post.service.PostsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.List;


@RestController
@RequestMapping("/posts") 
public class PostsController {

    private final PostsService postsService;

    public PostsController(PostsService postsService) {
        this.postsService = postsService;
    }

    // 현재 사용자 계정 ID 
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : null;
    }

    /** 게시판 리스트 조회 */
    @GetMapping // GET /posts?page=...&size=...&searchVal=...
    public Page<PostsListDto> getPostsList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchVal) {

        if (searchVal == null || searchVal.isBlank()) { 
            return postsService.getPostPage(page, size);
        } else {
            return postsService.getPostSrchVal(searchVal, page, size);
        }
    }

    /** 게시글 상세내용 조회 */
    @GetMapping("/{postId}") // GET /posts/{postId}
    public ResponseEntity<Posts> getPostDetail(
            @PathVariable Long postId,
            HttpServletRequest request, 
            HttpServletResponse response) { 
        try {
            Posts post = postsService.getPostDetail(postId, request, response);
            return ResponseEntity.ok(post);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
        }
    }

    /** 신규 게시글 저장 */
    @PostMapping // POST /posts
    public ResponseEntity<Void> savePost(@Valid @RequestBody PostsSaveDto dto) {
        String userId = getCurrentUserId();
        postsService.savePost(dto,userId);
        return ResponseEntity.status(HttpStatus.CREATED).build(); 
    }

    /** 게시글 내용 수정 */
    @PutMapping("/{postId}") // PUT /posts/{postId} 
    public ResponseEntity<Void> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostsUpdateDto dto) {
        String userId = getCurrentUserId();       
        postsService.updatePost(postId, dto, userId);
        return ResponseEntity.ok().build();
    }

    /** 게시글 내용 삭제 */
    @DeleteMapping("/{postId}") // DELETE /posts/{postId}
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        String userId = getCurrentUserId();    
        postsService.deletePost(postId, userId);
        return ResponseEntity.noContent().build(); 
    }

    /** 댓글 리스트 조회 */
    @GetMapping("/{postId}/comments") // GET /posts/{postId}/comments
    public ResponseEntity<List<CommentsListDto>> getCommentsList(@PathVariable Long postId) {
        try {
            List<CommentsListDto> commentsList = postsService.getCommentsList(postId);
            return ResponseEntity.ok(commentsList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found (예: 게시글을 찾을 수 없을 때)
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** 댓글 저장 */
    @PostMapping("/{postId}/comments") // POST /posts/{postId}/comments
    public ResponseEntity<Void> saveComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentsSaveDto dto) {
        String userId = getCurrentUserId();
        postsService.saveComment(postId, dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** 댓글 수정 */
    @PutMapping("/{postId}/comments/{commentId}") // PUT /posts/{postId}/comments/{commentId}
    public ResponseEntity<Void> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentsUpdateDto dto) {     
        postsService.updateComment(commentId, dto);
        return ResponseEntity.ok().build();
    }

    /** 댓글 삭제 */
    @DeleteMapping("/{postId}/comments/{commentId}") // DELETE /posts/{postId}/comments/{commentId}
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId, 
            @PathVariable Long commentId) {
        postsService.deleteComment(commentId); 
        return ResponseEntity.noContent().build();
    }
}