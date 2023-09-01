package com.winnow.bestchoice.service;

import com.winnow.bestchoice.config.jwt.TokenProvider;
import com.winnow.bestchoice.config.properties.JwtProperties;
import com.winnow.bestchoice.entity.Comment;
import com.winnow.bestchoice.entity.CommentLike;
import com.winnow.bestchoice.entity.Member;
import com.winnow.bestchoice.entity.Post;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.model.request.CreateCommentForm;
import com.winnow.bestchoice.repository.CommentLikeRepository;
import com.winnow.bestchoice.repository.CommentRepository;
import com.winnow.bestchoice.repository.MemberRepository;
import com.winnow.bestchoice.repository.PostRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks CommentService commentService;
    @Mock MemberRepository memberRepository;
    @Mock PostRepository postRepository;
    @Mock CommentRepository commentRepository;
    @Mock CommentLikeRepository commentLikeRepository;
    @Mock TokenProvider tokenProvider;
    Authentication authentication = setAuthentication();
    long memberId = 1L;
    long postId = 1L;
    long commentId = 10L;

    @Test
    @DisplayName("댓글 작성 성공")
    void createCommentSuccess() {
        CreateCommentForm form = new CreateCommentForm();
        given(tokenProvider.getMemberId(any())).willReturn(memberId);
        given(memberRepository.existsById(any())).willReturn(true);
        given(postRepository.existsById(any())).willReturn(true);
        given(memberRepository.getReferenceById(anyLong())).willReturn(new Member(memberId));
        given(postRepository.getReferenceById(anyLong())).willReturn(new Post(postId));

        commentService.createComment(authentication, postId, form);

        verify(commentRepository).save(any(Comment.class));
        verify(postRepository).plusCommentCountById(postId);
    }

    @Test
    @DisplayName("댓글 작성 실패 - 존재하지 않는 게시글")
    void createCommentFail() {
        CreateCommentForm form = new CreateCommentForm();
        given(tokenProvider.getMemberId(any())).willReturn(memberId);
        given(memberRepository.existsById(any())).willReturn(true);
        given(postRepository.existsById(any())).willReturn(false);

        CustomException e = assertThrows(CustomException.class,
                () -> commentService.createComment(authentication, postId, form));

        Assertions.assertEquals(e.getErrorCode(), ErrorCode.POST_NOT_FOUND);
    }

    @Test
    @DisplayName("댓글 좋아요 성공")
    void likeCommentSuccess() {
        given(tokenProvider.getMemberId(any())).willReturn(memberId);
        given(memberRepository.existsById(any())).willReturn(true);
        given(commentRepository.existsById(any())).willReturn(true);
        given(memberRepository.getReferenceById(anyLong())).willReturn(new Member(memberId));
        given(commentRepository.getReferenceById(anyLong())).willReturn(new Comment());
        given(commentLikeRepository.existsByCommentAndMember(any(), any())).willReturn(false);

        commentService.likeComment(authentication, commentId);

        verify(commentLikeRepository).save(any(CommentLike.class));
        verify(commentRepository).plusLikeCountById(commentId);
    }

    @Test
    @DisplayName("댓글 좋아요 실패 - 이미 좋아요한 댓글")
    void likeCommentFail() {
        given(tokenProvider.getMemberId(any())).willReturn(memberId);
        given(memberRepository.existsById(any())).willReturn(true);
        given(commentRepository.existsById(any())).willReturn(true);
        given(memberRepository.getReferenceById(anyLong())).willReturn(new Member(memberId));
        given(commentRepository.getReferenceById(anyLong())).willReturn(new Comment());
        given(commentLikeRepository.existsByCommentAndMember(any(), any())).willReturn(true);

        CustomException e = assertThrows(CustomException.class,
                () -> commentService.likeComment(authentication, commentId));

        assertEquals(e.getErrorCode(), ErrorCode.INVALID_REQUEST);
    }

    @Test
    @DisplayName("댓글 좋아요 취소 성공")
    void unlikeCommentSuccess() {
        CommentLike commentLike = new CommentLike();
        given(tokenProvider.getMemberId(any())).willReturn(memberId);
        given(memberRepository.existsById(any())).willReturn(true);
        given(commentRepository.existsById(any())).willReturn(true);
        given(memberRepository.getReferenceById(anyLong())).willReturn(new Member(memberId));
        given(commentRepository.getReferenceById(anyLong())).willReturn(new Comment());
        given(commentLikeRepository.findByCommentAndMember(any(), any())).willReturn(Optional.of(commentLike));

        commentService.unlikeComment(authentication, commentId);

        verify(commentLikeRepository).delete(commentLike);
        verify(commentRepository).minusLikeCountById(commentId);
    }

    @Test
    @DisplayName("댓글 좋아요 취소 실패 - 좋아요 누르지 않은 댓글")
    void unlikeCommentFail() {
        given(tokenProvider.getMemberId(any())).willReturn(memberId);
        given(memberRepository.existsById(any())).willReturn(true);
        given(commentRepository.existsById(any())).willReturn(true);
        given(memberRepository.getReferenceById(anyLong())).willReturn(new Member(memberId));
        given(commentRepository.getReferenceById(anyLong())).willReturn(new Comment());
        given(commentLikeRepository.findByCommentAndMember(any(), any())).willReturn(Optional.empty());

        CustomException e = assertThrows(CustomException.class,
                () -> commentService.unlikeComment(authentication, commentId));
    }

    Authentication setAuthentication() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setIssuer("issuer");
        jwtProperties.setSecretKey("secretKey");
        TokenProvider tokenProvider = new TokenProvider(jwtProperties);

        Member member = Member.builder().id(1L).email("test@email.com").build();
        Duration expiredAt = Duration.ofDays(1);
        String token = tokenProvider.generateToken(member, expiredAt);
        return tokenProvider.getAuthentication(token);
    }
}