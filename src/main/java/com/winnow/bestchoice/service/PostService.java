package com.winnow.bestchoice.service;

import com.winnow.bestchoice.config.jwt.TokenProvider;
import com.winnow.bestchoice.entity.*;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.model.request.CreatePostForm;
import com.winnow.bestchoice.model.response.PostDetailRes;
import com.winnow.bestchoice.repository.*;
import com.winnow.bestchoice.type.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final PostLikeRepository postLikeRepository;
    private final ChoiceRepository choiceRepository;
    private final TokenProvider tokenProvider;


    public PostDetailRes createPost(CreatePostForm createPostForm, List<MultipartFile> files, Authentication authentication) { // 최적화 - tag 한 번에?
        if (files.size() > 5) { //첨부파일 5개 초과하는 경우
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        long memberId = tokenProvider.getMemberId(authentication);

        Member member = memberRepository.findById(memberId).
                orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = createPostForm.toEntity(member);
        postRepository.save(post);

        List<String> tags = createPostForm.getTags();
        List<PostTag> postTags = new ArrayList<>();

        for (String tagName : tags) { //존재하는 태그면 가져오고 없으면 태그 생성
            Tag tag = tagRepository.findByName(tagName).orElseGet(() -> tagRepository.save(new Tag(tagName)));
            postTags.add(new PostTag(post, tag));
        }

        if (!postTags.isEmpty()) {
            postTagRepository.saveAll(postTags);
        }

        //TODO S3 저장 로직 구현
        ArrayList<String> resources = new ArrayList<>();

        PostDetailRes postDetail = PostDetailRes.of(post);
        postDetail.setTags(tags);
        postDetail.setResources(resources);

        return postDetail;
    }

    public void likePost(Authentication authentication, long postId) { //최적화 @DynamicUpdate?
        Long memberId = tokenProvider.getMemberId(authentication);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (postLikeRepository.existsByPostAndMember(post, member)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        post.setLikeCount(post.getLikeCount() + 1);
        postLikeRepository.save(new PostLike(member, post));
    }

    public void unlikePost(Authentication authentication, long postId) { //최적화
        Long memberId = tokenProvider.getMemberId(authentication);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        PostLike postLike = postLikeRepository.findByPostAndMember(post, member)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        post.setLikeCount(post.getLikeCount() - 1);
        postLikeRepository.delete(postLike);
    }

    public void choiceOption(Authentication authentication, long postId, Option choice) {
        Long memberId = tokenProvider.getMemberId(authentication);

        if (!memberRepository.existsById(memberId)) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        if (!postRepository.existsById(postId)) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        Member member = memberRepository.getReferenceById(memberId);
        Post post = postRepository.getReferenceById(postId);

        if (choiceRepository.existsByPostAndMember(post, member)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        //TODO update optionCount in post

        choiceRepository.save(Choice.builder()
                .member(member)
                .post(post)
                .option(choice).build());
    }

    public PostDetailRes getPostDetail(long postId) { // 최적화
        Post post = postRepository.findWithMemberById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        PostDetailRes postDetail = PostDetailRes.of(post);
        postDetail.setTags(Collections.emptyList());
        postDetail.setResources(Collections.emptyList());

        return postDetail;
    }
}
