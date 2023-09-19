package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.annotation.LoginMemberId;
import com.winnow.bestchoice.model.request.CreateCommentForm;
import com.winnow.bestchoice.model.response.CommentRes;
import com.winnow.bestchoice.service.CommentService;
import com.winnow.bestchoice.type.CommentSort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     *  댓글 작성
     */
    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<?> createComment(@LoginMemberId long memberId, @PathVariable long postId,
                                           @RequestBody @Valid CreateCommentForm commentForm) {
        commentService.createComment(memberId, postId, commentForm);
        return ResponseEntity.ok().build();
    }

    /**
     *  댓글 좋아요
     */
    @PostMapping("/api/comments/{commentId}/like")
    public ResponseEntity<?> likeComment(@LoginMemberId long memberId, @PathVariable long commentId) {
        commentService.likeComment(memberId, commentId);
        return ResponseEntity.ok().build();
    }

    /**
     *  댓글 좋아요 취소
     */
    @PostMapping("/api/comments/{commentId}/unlike")
    public ResponseEntity<?> unlikeComment(@LoginMemberId long memberId, @PathVariable long commentId) {
        commentService.unlikeComment(memberId, commentId);
        return ResponseEntity.ok().build();
    }

    /**
     *  댓글 삭제
     */
    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@LoginMemberId long memberId, @PathVariable long commentId) {
        commentService.deleteComment(memberId, commentId);
        return ResponseEntity.ok().build();
    }

    /**
     *  댓글 목록 조회
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Page<CommentRes>> getComments(Authentication authentication, @PathVariable long postId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(defaultValue = "LATEST") CommentSort sort) {
        return ResponseEntity.ok(commentService.getComments(authentication, postId, page, size, sort));
    }
}
