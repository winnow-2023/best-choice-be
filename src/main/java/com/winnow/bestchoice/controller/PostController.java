package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.model.request.CreateCommentForm;
import com.winnow.bestchoice.model.request.CreatePostForm;
import com.winnow.bestchoice.model.response.PostDetailRes;
import com.winnow.bestchoice.service.CommentService;
import com.winnow.bestchoice.service.PostService;
import com.winnow.bestchoice.type.CommentSort;
import com.winnow.bestchoice.type.MyPageSort;
import com.winnow.bestchoice.type.Option;
import com.winnow.bestchoice.type.PostSort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/posts")
@Validated
@Slf4j
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<?> createPost(Authentication authentication,
                                        @RequestPart("data") @Valid CreatePostForm createPostForm,
                                        @RequestPart(required = false) @Size(max = 5) List<MultipartFile> files) {

        PostDetailRes postDetail = postService.createPost(createPostForm, files, authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body(postDetail);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(Authentication authentication, @PathVariable long postId) {

        postService.likePost(authentication, postId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/unlike")
    public ResponseEntity<?> unlikePost(Authentication authentication, @PathVariable long postId) {

        postService.unlikePost(authentication, postId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/choice")
    public ResponseEntity<?> choiceOption(Authentication authentication, @PathVariable long postId,
                                          @RequestParam Option option) {

        postService.choiceOption(authentication, postId, option);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> createComment(Authentication authentication, @PathVariable long postId,
                                           @RequestBody @Valid CreateCommentForm commentForm) {

        commentService.createComment(authentication, postId, commentForm);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostDetail(Authentication authentication, @PathVariable long postId) {
        return ResponseEntity.ok(postService.getPostDetail(authentication, postId));
    }

    @GetMapping
    public ResponseEntity<?> getPosts(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(defaultValue = "LATEST") PostSort sort) {

        return ResponseEntity.ok().body(postService.getPosts(page, size, sort));
    }

    @GetMapping("/tag")
    public ResponseEntity<?> getPostsByTag(@RequestParam @Size(min = 2, max = 10) String tag,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(postService.getPostsByTag(page, size, tag));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable long postId, @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(defaultValue = "LATEST") CommentSort sort) {

        return ResponseEntity.ok(commentService.getComments(postId, page, size, sort));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyPage(Authentication authentication,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(defaultValue = "LIKES") MyPageSort sort) {

        return ResponseEntity.ok().body(postService.getMyPage(authentication, page, size, sort));
    }
}
