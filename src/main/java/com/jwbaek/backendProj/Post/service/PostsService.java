package com.jwbaek.backendProj.Post.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

import com.jwbaek.backendProj.Post.entity.Comments;
import com.jwbaek.backendProj.Post.entity.CommentsListDto;
import com.jwbaek.backendProj.Post.entity.CommentsRepository;
import com.jwbaek.backendProj.Post.entity.CommentsSaveDto;
import com.jwbaek.backendProj.Post.entity.CommentsUpdateDto;
import com.jwbaek.backendProj.Post.entity.Posts;
import com.jwbaek.backendProj.Post.entity.PostsHis;
import com.jwbaek.backendProj.Post.entity.PostsHisRepository;
import com.jwbaek.backendProj.Post.entity.PostsListDto;
import com.jwbaek.backendProj.Post.entity.PostsRepository;
import com.jwbaek.backendProj.Post.entity.PostsSaveDto;
import com.jwbaek.backendProj.Post.entity.PostsUpdateDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;

import jakarta.transaction.Transactional;
@Service
@Transactional
public class PostsService {

    private final PostsRepository postsRepository;
    private final PostsHisRepository postsHisRepository;
    private final CommentsRepository commentsRepository;

    public PostsService(PostsRepository postsRepository, PostsHisRepository postsHisRepository, CommentsRepository commentsRepository) {
        this.postsRepository = postsRepository;
        this.postsHisRepository = postsHisRepository;
        this.commentsRepository = commentsRepository;
    }

    // --- 페이지네이션 Pageable 객체 생성 중복 로직 분리 ---
    private Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by("createdDate").descending());
    }

    private Page<PostsListDto> convertToDtoPage(Page<Posts> postsPage, Pageable pageable) {
        List<PostsListDto> dtoList = new ArrayList<>();
        long total = postsPage.getTotalElements();

        for (int i = 0; i < postsPage.getContent().size(); i++) {
            Posts post = postsPage.getContent().get(i);

            long orderId = total - (postsPage.getNumber() * pageable.getPageSize() + i);

            PostsListDto dto = new PostsListDto(
                    post.getId(),
                    post.getTitle(),
                    post.getAuthor(),
                    post.getCreatedDate(),
                    post.getViewCnt()
            );
            dto.setOrderId(orderId);
            dtoList.add(dto);
        }

        return new PageImpl<>(dtoList, pageable, total);
    }

    // 검색어 없을 때
    public Page<PostsListDto> getPostPage(int page, int size) {
        Pageable pageable = createPageable(page, size); // createPageable 메서드 사용
        return convertToDtoPage(postsRepository.findAll(pageable), pageable);
    }

    // 검색어 있을 때
    public Page<PostsListDto> getPostSrchVal(String srchVal, int page, int size) {
        String searchVal = "%" + srchVal.trim() + "%";
        Pageable pageable = createPageable(page, size); // createPageable 메서드 사용
        return convertToDtoPage(postsRepository.findByTitleLikePage(searchVal, pageable), pageable);
    }

    /** 게시글 상세 조회 (조회수 증가 로직 포함) */
    public Posts getPostDetail(Long id, HttpServletRequest request, HttpServletResponse response) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        // 조회수 중복 방지 : 세션 기반 체크
        HttpSession session = request.getSession();
        Set<Long> viewedPostIds = (Set<Long>) session.getAttribute("viewed_post_list");
        if (viewedPostIds == null) {
            viewedPostIds = new HashSet<>();
            session.setAttribute("viewed_post_list", viewedPostIds);
        }

        // 쿠키 체크
        String cookieName = "viewed_post_" + id;
        boolean viewedByCookie = false;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    viewedByCookie = true;
                    break;
                }
            }
        }

        if (!viewedPostIds.contains(id) && !viewedByCookie) {
            post.setViewCnt(post.getViewCnt() + 1);
            viewedPostIds.add(id);

            Cookie cookie = new Cookie(cookieName, "true");
            cookie.setMaxAge(60 * 60 * 24);
            cookie.setPath("/");
            response.addCookie(cookie);
        }

        return post;
    }

    /** 게시글 저장 */
    public void savePost(PostsSaveDto dto, String userId) {
        if (dto == null || dto.getTitle().isBlank() || dto.getContent().isBlank()) {
            throw new IllegalArgumentException("유효하지 않은 게시글 데이터입니다.");
        }

        Posts post = Posts.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(userId)
                .viewCnt(0L)
                .build();

        Posts savedPost = postsRepository.save(post); // save 호출의 반환 값을 받아 사용

        PostsHis postHis = PostsHis.builder()
                .postId(savedPost.getId()) // 저장된 게시글의 ID 사용
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(userId)
                .status("C")
                .modUserId(userId)
                .build();

        postsHisRepository.save(postHis);
    }

    /** 게시글 수정 */
    public void updatePost(Long postId, PostsUpdateDto dto, String userId) {
        if (dto == null || dto.getTitle().isBlank() || dto.getContent().isBlank()) {
            throw new IllegalArgumentException("유효하지 않은 게시글 데이터입니다.");
        }

        // 게시글 조회 및 존재 여부 확인 : post 객체는 영속성 컨텍스트에 의해 관리(더티체킹)
        Posts post = postsRepository.findById(postId) // postId 파라미터 사용
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: ID " + postId));

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setUpdatedDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")));

        // 게시글 히스토리
        PostsHis postsHis = PostsHis.builder()
                .postId(post.getId())
                .title(dto.getTitle()) // 업데이트된 제목
                .content(dto.getContent()) // 업데이트된 내용
                .author(post.getAuthor()) // 원래 게시글 작성자
                .modUserId(userId)
                .status("U") // 'U'
                .build();

        postsHisRepository.save(postsHis);

    }

    /** 게시글 삭제 */
    public void deletePost(Long postId, String userId) {
        Posts posts = postsRepository.findById(postId) // postId 파라미터 사용
                .orElseThrow(() -> new RuntimeException("ID : " + postId + "번 게시글을 찾을 수 없습니다."));

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("사용자 정보가 존재하지 않습니다.");
        }

        PostsHis postsHis = PostsHis.builder()
                .postId(posts.getId())
                .title(posts.getTitle())
                .content(posts.getContent())
                .author(posts.getAuthor())
                .modUserId(userId)
                .status("D")
                .build();

        postsHisRepository.save(postsHis);
        postsRepository.delete(posts);
    }

    /** 댓글 리스트 */
    public List<CommentsListDto> getCommentsList(Long postId) {

        List<Comments> comments = commentsRepository.findByPostId(postId); // 댓글 조회

        List<CommentsListDto> dtoList = comments.stream()
            .map(comment -> CommentsListDto.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .author(comment.getAuthor())
                .content(comment.getContent())
                .comParentId(comment.getComParentId())
                .likeCnt(comment.getLikeCnt())
                .updatedDate(comment.getUpdatedDate())
                .reCommentList(new ArrayList<>())
                .build())
            .collect(Collectors.toList());

        // id 기준으로 Map 생성
        Map<Long, CommentsListDto> dtoMap = dtoList.stream()
            .collect(Collectors.toMap(CommentsListDto::getId, dto -> dto));

        List<CommentsListDto> rootComments = new ArrayList<>();
        for (CommentsListDto dto : dtoList) {
            if (dto.getComParentId() == 0) { // 최상위 댓글
                rootComments.add(dto);
            } else {
                CommentsListDto parent = dtoMap.get(dto.getComParentId());
                if (parent != null) {
                    parent.getReCommentList().add(dto); // 상위 댓글에 추가
                }
            }
        }

        return rootComments;

    }

    /** 댓글 저장 */
    public void saveComment(Long postId, CommentsSaveDto dto, String userId) {

        if (dto == null || dto.getContent() == null || dto.getContent().isBlank()) {
            throw new IllegalArgumentException("유효하지 않은 댓글 데이터입니다.");
        }

        if (dto.getComParentId() != null && !commentsRepository.existsById(dto.getComParentId())) {
            throw new IllegalArgumentException("존재하지 않는 상위 댓글입니다.");
        }

        Comments comments = Comments.builder()
                .postId(postId) // postId 설정
                .content(dto.getContent())
                .author(userId)
                .comParentId(dto.getComParentId())
                .likeCnt(0L)
                .isDeleted(false)
                .build();

        commentsRepository.save(comments);
    }

    /** 댓글 수정 */
    public void updateComment(Long commentId, CommentsUpdateDto dto) {

        if (dto == null || dto.getContent() == null || dto.getContent().isBlank()) {
            throw new IllegalArgumentException("유효하지 않은 댓글 데이터입니다.");
        }

        Comments comment = commentsRepository.findById(commentId) // commentId 파라미터 사용
            .orElseThrow(() -> new RuntimeException("댓글 없음"));

        comment.setContent(dto.getContent());
        comment.setUpdatedDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")));

    }

    /** 댓글 삭제 */
    public void deleteComment(Long commentId) {
        Comments comments = commentsRepository.findById(commentId) // commentId 파라미터 사용
                .orElseThrow(() -> new RuntimeException("ID : " + commentId + "번 댓글을 찾을 수 없습니다."));
        
        if(comments.getComParentId()==0L&& comments.getComParentId().equals(0L)){ //최상위 댓글일 경우 -> 하위 댓글 삭제
            commentsRepository.deleteAllByComParentId(commentId);
        }

        commentsRepository.delete(comments);
        
    }
}