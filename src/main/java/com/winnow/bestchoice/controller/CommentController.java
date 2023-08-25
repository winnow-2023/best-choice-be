package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{commentId}/like")
    public ResponseEntity<?> likeComment(Authentication authentication, @PathVariable long commentId) {
        commentService.likeComment(authentication, commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{commentId}/unlike")
    public ResponseEntity<?> unlikeComment(Authentication authentication, @PathVariable long commentId) {
        commentService.unlikeComment(authentication, commentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(Authentication authentication, @PathVariable long commentId) {
        commentService.deleteComment(authentication, commentId);
        return ResponseEntity.ok().build();
    }
}
