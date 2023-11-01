package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.annotation.LoginMemberId;
import com.winnow.bestchoice.model.request.CreatePostForm;
import com.winnow.bestchoice.model.response.PostDetailRes;
import com.winnow.bestchoice.model.response.PostRes;
import com.winnow.bestchoice.service.PostService;
import com.winnow.bestchoice.type.MyPageSort;
import com.winnow.bestchoice.type.Option;
import com.winnow.bestchoice.type.PostSort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class PostController {

    private final PostService postService;

    /**
     *  게시글 작성 - 파일 첨부 가능
     */
    @PostMapping("/api/posts")
    public ResponseEntity<PostDetailRes> createPost(@LoginMemberId long memberId,
                                        @RequestPart("data") @Valid CreatePostForm createPostForm,
                                        @RequestPart(name = "imageFile", required = false) @Size(max = 5) List<MultipartFile> imageFiles,
                                        @RequestPart(name = "videoFile", required = false) @Size(max = 5) List<MultipartFile> videoFiles) {
        PostDetailRes postDetail = postService.createPost(createPostForm, imageFiles, videoFiles, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(postDetail);
    }

    /**
     *  게시글 좋아요
     */
    @PostMapping("/api/posts/{postId}/like")
    public ResponseEntity<?> likePost(@LoginMemberId long memberId, @PathVariable long postId) {
        postService.likePost(memberId, postId);
        return ResponseEntity.ok().build();
    }

    /**
     *  게시글 좋아요 취소
     */
    @PostMapping("/api/posts/{postId}/unlike")
    public ResponseEntity<?> unlikePost(@LoginMemberId long memberId, @PathVariable long postId) {
        postService.unlikePost(memberId, postId);
        return ResponseEntity.ok().build();
    }

    /**
     *  게시글 옵션 선택 (A or B)
     */
    @PostMapping("/api/posts/{postId}/choice")
    public ResponseEntity<?> choiceOption(@LoginMemberId long memberId, @PathVariable long postId,
                                          @RequestParam Option option) {
        postService.choiceOption(memberId, postId, option);
        return ResponseEntity.ok().build();
    }

    /**
     *  게시글 삭제
     */
    @DeleteMapping("/api/posts/{postId}")
    public ResponseEntity<?> deletePost(@LoginMemberId long memberId, @PathVariable long postId) {
        postService.deletePost(memberId, postId);
        return ResponseEntity.ok().build();
    }

    /**
     *  게시글 상세 조회
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostDetailRes> getPostDetail(Authentication authentication, @PathVariable long postId) {
        return ResponseEntity.ok(postService.getPostDetail(authentication, postId));
    }

    /**
     *  게시글 목록 조회 (최신순, 좋아요순, 댓글순, 인기글 달성일)
     */
    @GetMapping("/posts")
    public ResponseEntity<Slice<PostRes>> getPosts(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(defaultValue = "LATEST") PostSort sort) {
        return ResponseEntity.ok().body(postService.getPosts(page, size, sort));
    }

    /**
     *  게시글 목록 tag로 조회
     */
    @GetMapping("/posts/tag")
    public ResponseEntity<Slice<PostRes>> getPostsByTag(@RequestParam @Size(min = 2, max = 10) String tag,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getPostsByTag(page, size, tag));
    }

    /**
     *  myPage 게시글 목록 조회 (좋아요, 댓글, 투표, 작성한 게시글)
     */
    @GetMapping("/api/posts/my")
    public ResponseEntity<Slice<PostRes>> getMyPage(@LoginMemberId long memberId,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(defaultValue = "LIKES") MyPageSort sort) {
        return ResponseEntity.ok().body(postService.getMyPage(memberId, page, size, sort));
    }

    /**
     *  게시글 신고
     */
    @PostMapping("/api/posts/{postId}/report")
    public ResponseEntity<?> reportPost(@LoginMemberId long memberId, @PathVariable long postId) {
        postService.reportPost(memberId, postId);
        return ResponseEntity.ok().build();
    }
}
