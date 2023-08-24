package com.winnow.bestchoice.service;

import com.winnow.bestchoice.config.jwt.TokenProvider;
import com.winnow.bestchoice.entity.Member;
import com.winnow.bestchoice.entity.Post;
import com.winnow.bestchoice.entity.PostTag;
import com.winnow.bestchoice.entity.Tag;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.model.request.CreatePostForm;
import com.winnow.bestchoice.model.response.PostDetailRes;
import com.winnow.bestchoice.repository.MemberRepository;
import com.winnow.bestchoice.repository.PostRepository;
import com.winnow.bestchoice.repository.PostTagRepository;
import com.winnow.bestchoice.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final TokenProvider tokenProvider;


    public PostDetailRes createPost(CreatePostForm createPostForm, List<MultipartFile> files, Authentication authentication) {
        if (files.size() > 5) { //첨부파일 5개 초과하는 경우
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        long memberId = tokenProvider.getMemberId(authentication);

        Member member = memberRepository.findById(memberId).
                orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Post post = createPostForm.toEntity(member);
        postRepository.save(post); //post 저장

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
        postDetail.setMemberId(member.getId());
        postDetail.setNickname(member.getNickname());
        postDetail.setTags(tags);
        postDetail.setResources(resources);

        return postDetail;
    }
}
