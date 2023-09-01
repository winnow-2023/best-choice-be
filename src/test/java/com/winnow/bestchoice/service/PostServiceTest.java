package com.winnow.bestchoice.service;

import com.winnow.bestchoice.config.jwt.TokenProvider;
import com.winnow.bestchoice.config.properties.JwtProperties;
import com.winnow.bestchoice.entity.Member;
import com.winnow.bestchoice.entity.PostLike;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.repository.MemberRepository;
import com.winnow.bestchoice.repository.PostLikeRepository;
import com.winnow.bestchoice.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import java.time.Duration;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks PostService postService;
    @Mock MemberRepository memberRepository;
    @Mock PostRepository postRepository;
    @Mock PostLikeRepository postLikeRepository;
    @Mock TokenProvider tokenProvider;
    Authentication authentication = setAuthentication();

    @DisplayName("게시글 좋아요 성공")
    @Test
    void likePostSuccess() {
        long memberId = 1L;
        long postId = 1L;

        given(tokenProvider.getMemberId(any())).willReturn(memberId);
        given(memberRepository.existsById(any())).willReturn(true);
        given(postRepository.existsById(any())).willReturn(true);
        given(postLikeRepository.existsByPost_IdAndMember_Id(anyLong(), anyLong())).willReturn(false);

        postService.likePost(authentication, postId);

        verify(postLikeRepository).save(any());
        verify(postRepository).plusLikeCountById(postId);
    }

    @DisplayName("게시글 좋아요 실패 - 이미 좋아요한 게시글")
    @Test
    void likePostFail() {
        long memberId = 1L;
        long postId = 1L;

        given(tokenProvider.getMemberId(any())).willReturn(memberId);
        given(memberRepository.existsById(any())).willReturn(true);
        given(postRepository.existsById(any())).willReturn(true);
        given(postLikeRepository.existsByPost_IdAndMember_Id(anyLong(), anyLong())).willReturn(true);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> postService.likePost(authentication, postId));

        Assertions.assertEquals(e.getErrorCode(), ErrorCode.INVALID_REQUEST);
    }

    @DisplayName("게시글 좋아요 취소 성공")
    @Test
    void unlikePostSuccess() {
        long memberId = 1L;
        long postId = 1L;
        PostLike postLike = new PostLike();

        given(tokenProvider.getMemberId(any())).willReturn(memberId);
        given(postLikeRepository.findByPost_IdAndMember_Id(anyLong(), anyLong())).willReturn(Optional.of(postLike));

        postService.unlikePost(authentication, postId);

        verify(postLikeRepository).delete(postLike);
        verify(postRepository).minusLikeCountById(postId);
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