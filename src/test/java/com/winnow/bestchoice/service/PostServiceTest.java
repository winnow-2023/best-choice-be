package com.winnow.bestchoice.service;

import com.winnow.bestchoice.config.jwt.TokenProvider;
import com.winnow.bestchoice.config.properties.JwtProperties;
import com.winnow.bestchoice.entity.Choice;
import com.winnow.bestchoice.entity.Member;
import com.winnow.bestchoice.entity.Post;
import com.winnow.bestchoice.entity.PostLike;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.model.dto.PostDetailDto;
import com.winnow.bestchoice.model.dto.PostSummaryDto;
import com.winnow.bestchoice.model.response.PostDetailRes;
import com.winnow.bestchoice.model.response.PostRes;
import com.winnow.bestchoice.repository.*;
import com.winnow.bestchoice.type.MyPageSort;
import com.winnow.bestchoice.type.Option;
import com.winnow.bestchoice.type.PostSort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.core.Authentication;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks PostService postService;
    @Mock MemberRepository memberRepository;
    @Mock PostRepository postRepository;
    @Mock PostQueryRepository postQueryRepository;
    @Mock PostLikeRepository postLikeRepository;
    @Mock ChoiceRepository choiceRepository;
    @Mock TokenProvider tokenProvider;
    Authentication authentication = setAuthentication();

    long memberId = 1L;
    long postId = 1L;

    @Test
    @DisplayName("게시글 좋아요 성공")
    void likePostSuccess() {
        given(tokenProvider.getMemberId(any())).willReturn(memberId);
        given(memberRepository.existsById(any())).willReturn(true);
        given(postRepository.existsById(any())).willReturn(true);
        given(postLikeRepository.existsByPost_IdAndMember_Id(anyLong(), anyLong())).willReturn(false);

        postService.likePost(authentication, postId);

        verify(postLikeRepository).save(any());
        verify(postRepository).plusLikeCountById(postId);
    }

    @Test
    @DisplayName("게시글 좋아요 실패 - 이미 좋아요한 게시글")
    void likePostFail() {
        given(tokenProvider.getMemberId(any())).willReturn(memberId);
        given(memberRepository.existsById(any())).willReturn(true);
        given(postRepository.existsById(any())).willReturn(true);
        given(postLikeRepository.existsByPost_IdAndMember_Id(anyLong(), anyLong())).willReturn(true);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> postService.likePost(authentication, postId));

        assertEquals(e.getErrorCode(), ErrorCode.INVALID_REQUEST);
    }

    @Test
    @DisplayName("게시글 좋아요 취소 성공")
    void unlikePostSuccess() {
        PostLike postLike = new PostLike();
        given(tokenProvider.getMemberId(any())).willReturn(memberId);
        given(postLikeRepository.findByPost_IdAndMember_Id(anyLong(), anyLong())).willReturn(Optional.of(postLike));

        postService.unlikePost(authentication, postId);

        verify(postLikeRepository).delete(postLike);
        verify(postRepository).minusLikeCountById(postId);
    }

    @Test
    @DisplayName("게시글 좋아요 취소 실패 - 좋아요 누르지 않은 게시글에 취소 시도")
    void unlikePostFail() {
        given(tokenProvider.getMemberId(any())).willReturn(memberId);
        given(postLikeRepository.findByPost_IdAndMember_Id(anyLong(), anyLong())).willReturn(Optional.empty());

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> postService.unlikePost(authentication, postId));

        assertEquals(e.getErrorCode(), ErrorCode.INVALID_REQUEST);
    }

    @Test
    @DisplayName("게시글 옵션(A or B) 선택 성공")
    void choiceOptionSuccess() {
        given(tokenProvider.getMemberId(any())).willReturn(memberId);
        given(memberRepository.existsById(any())).willReturn(true);
        given(postRepository.existsById(any())).willReturn(true);
        given(memberRepository.getReferenceById(anyLong())).willReturn(new Member(memberId));
        given(postRepository.getReferenceById(anyLong())).willReturn(new Post(postId));
        given(choiceRepository.existsByPostAndMember(any(), any())).willReturn(false);

        postService.choiceOption(authentication, postId, Option.A);

        verify(choiceRepository).save(any(Choice.class));
        verify(postRepository).plusACountById(postId);
        verify(postRepository, never()).plusBCountById(postId);
    }

    @Test
    @DisplayName("게시글 옵션(A or B) 선택 실패 - 이미 옵션 선택한 게시글")
    void choiceOptionFail() {
        given(tokenProvider.getMemberId(any())).willReturn(memberId);
        given(memberRepository.existsById(any())).willReturn(true);
        given(postRepository.existsById(any())).willReturn(true);
        given(memberRepository.getReferenceById(anyLong())).willReturn(new Member(memberId));
        given(postRepository.getReferenceById(anyLong())).willReturn(new Post(postId));
        given(choiceRepository.existsByPostAndMember(any(), any())).willReturn(true);

        CustomException e = Assertions.assertThrows(CustomException.class,
                () -> postService.choiceOption(authentication, postId, Option.A));

        assertEquals(e.getErrorCode(), ErrorCode.INVALID_REQUEST);
    }

    @Test
    @DisplayName("게시글 상세 조회 성공")
    void getPostDetailSuccess() {
        PostDetailDto postDetailDto = new PostDetailDto();
        postDetailDto.setId(postId);
        given(tokenProvider.getMemberId(any())).willReturn(memberId);
        given(postQueryRepository.getPostDetail(anyLong(), anyLong())).willReturn(Optional.of(postDetailDto));

        PostDetailRes postDetail = postService.getPostDetail(authentication, postId);

        assertEquals(postDetail.getPostId(), postId);
    }

    @Test
    @DisplayName("게시글 상세 조회 실패 - 존재하지 않는 게시글")
    void getPostDetailFail() {
        PostDetailDto postDetailDto = new PostDetailDto();
        postDetailDto.setId(postId);

        given(tokenProvider.getMemberId(any())).willReturn(memberId);
        given(postQueryRepository.getPostDetail(anyLong(), anyLong())).willReturn(Optional.empty());

        CustomException e = assertThrows(CustomException.class,
                () -> postService.getPostDetail(authentication, postId));

        assertEquals(e.getErrorCode(), ErrorCode.POST_NOT_FOUND);
    }

    @Test
    @DisplayName("myPage 게시글 타입별 조회 성공")
    void getMyPageSuccess() {
        SliceImpl<PostSummaryDto> slice = new SliceImpl<>(Collections.emptyList());
        given(tokenProvider.getMemberId(any())).willReturn(memberId);
        given(postQueryRepository.getSliceFromChoices(any(), anyLong())).willReturn(slice);

        postService.getMyPage(authentication, 0, 10, MyPageSort.CHOICES);

        verify(postQueryRepository).getSliceFromChoices(any(), anyLong());
        verify(postQueryRepository, never()).getSliceFromComments(any(), anyLong());
        verify(postQueryRepository, never()).getSliceFromLikes(any(), anyLong());
        verify(postQueryRepository, never()).getSliceByMemberId(any(), anyLong());
    }

    @Test
    @DisplayName("게시글 타입별 조회 성공")
    void getPosts() {
        PostSort sort = PostSort.LIKES;
        SliceImpl<PostSummaryDto> dtos = new SliceImpl<>(List.of(new PostSummaryDto()));
        given(postQueryRepository.getSlice(any(), any())).willReturn(dtos);

        Slice<PostRes> slice = postService.getPosts(0, 10, sort);

        assertEquals(slice.getContent().size(), 1);
        verify(postQueryRepository).getSlice(any(), eq(sort.getType()));
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