package com.winnow.bestchoice.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.winnow.bestchoice.config.jwt.TokenProvider;
import com.winnow.bestchoice.entity.*;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.model.dto.PostDetailDto;
import com.winnow.bestchoice.model.dto.PostSummaryDto;
import com.winnow.bestchoice.model.request.CreatePostForm;
import com.winnow.bestchoice.model.response.PostDetailRes;
import com.winnow.bestchoice.model.response.PostRes;
import com.winnow.bestchoice.repository.*;
import com.winnow.bestchoice.type.MyPageSort;
import com.winnow.bestchoice.type.Option;
import com.winnow.bestchoice.type.PostSort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final PostLikeRepository postLikeRepository;
    private final ChoiceRepository choiceRepository;
    private final AttachmentRepository attachmentRepository;
    private final ReportRepository reportRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final AmazonS3Client amazonS3Client;
    private final TokenProvider tokenProvider;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${point.popularity}")
    private int popularityPoint;
    @Value("${point.report}")
    private int reportPoint;
    private final String IMAGE_PATH = "image/";
    private final String VIDEO_PATH = "video/";
    private final int MAX_ATTACHMENT_SIZE = 5;


    public PostDetailRes createPost(CreatePostForm createPostForm, List<MultipartFile> imageFiles, List<MultipartFile> videoFiles, long memberId) { // 최적화 - tag 한 번에?
        if (!ObjectUtils.isEmpty(imageFiles) && !ObjectUtils.isEmpty(videoFiles) &&
                imageFiles.size() + videoFiles.size() > MAX_ATTACHMENT_SIZE) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        Member member = memberRepository.findById(memberId).
                orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));// 쿼리 최적화
        Post post = postRepository.save(createPostForm.toEntity(member));

        List<String> tags = createPostForm.getTags();
        List<PostTag> postTags = new ArrayList<>();

        for (String tagName : tags) { //존재하는 태그면 가져오고 없으면 태그 생성
            Tag tag = tagRepository.findByName(tagName).orElseGet(() -> tagRepository.save(new Tag(tagName)));
            postTags.add(new PostTag(post, tag));
        }
        if (!postTags.isEmpty()) {
            postTagRepository.saveAll(postTags);
        }

        List<String> resources = new ArrayList<>();
        saveImageFiles(imageFiles, post.getId(), resources);
        saveVideoFiles(videoFiles, post.getId(), resources);

        List<Attachment> attachments = resources.stream()
                .map(url -> new Attachment(post, url))
                .collect(Collectors.toList());
        attachmentRepository.saveAll(attachments);

        return PostDetailRes.of(post, resources);
    }

    private void saveImageFiles(List<MultipartFile> imageFiles, long postId, List<String> resources) {
        saveFilesToS3(imageFiles, postId, resources, IMAGE_PATH);
    }

    private void saveVideoFiles(List<MultipartFile> videoFiles, long postId, List<String> resources) {
        saveFilesToS3(videoFiles, postId, resources, VIDEO_PATH);
    }

    private void saveFilesToS3(List<MultipartFile> files, long postId, List<String> resources, String path) {
        if (!ObjectUtils.isEmpty(files)) {
            try {
                for (MultipartFile file : files) {
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentType(file.getContentType());
                    metadata.setContentLength(file.getSize());
                    String fileName = path + postId + "/" + UUID.randomUUID();
                    amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
                    resources.add(fileName);
                }
            } catch (Exception e) {
                log.info("exception is occurred when S3 upload in createPost");
                throw new CustomException(ErrorCode.SERVER_ERROR);
            }
        }
    }

    public void likePost(long memberId, long postId) { //최적화
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (postLikeRepository.existsByPost_IdAndMember_Id(postId, memberId)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        postLikeRepository.save(new PostLike(new Member(memberId), post));
        if (ObjectUtils.isEmpty(post.getPopularityDate()) && post.getLikeCount() + 1 >= popularityPoint) {
            postQueryRepository.plusLikeCountAndSetPopularityById(postId);
        } else {
            postRepository.plusLikeCountById(postId);
        }
    }

    public void unlikePost(long memberId, long postId) { //최적화
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        PostLike postLike = postLikeRepository.findByPost_IdAndMember_Id(postId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        postLikeRepository.delete(postLike);
        if (!ObjectUtils.isEmpty(post.getPopularityDate()) && post.getLikeCount() <= popularityPoint) {
            postQueryRepository.minusLikeCountAndCancelPopularityById(postId);
        } else {
            postRepository.minusLikeCountById(postId);
        }
    }

    public void choiceOption(long memberId, long postId, Option choice) {
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

        choiceRepository.save(Choice.builder()
                .member(member)
                .post(post)
                .choices(choice).build());

        switch (choice) {
            case A : postRepository.plusACountById(postId); break;
            case B : postRepository.plusBCountById(postId); break;
            default: throw new IllegalStateException("Unexpected value: " + choice);
        }
    }

    public void reportPost(long memberId, long postId) {
        if (!postRepository.existsByIdAndDeletedFalse(postId)) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }
        Member member = memberRepository.getReferenceById(memberId);
        Post post = postRepository.getReferenceById(postId);

        if (reportRepository.existsByPostAndMember(post, member)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        reportRepository.save(new Report(member, post));
        if (reportRepository.countByPost(post) >= reportPoint) {
            postQueryRepository.deletePost(postId);
        }
    }

    public PostDetailRes getPostDetail(Authentication authentication, long postId) {
        Optional<PostDetailDto> postDetailDtoOptional;
        if (ObjectUtils.isEmpty(authentication)) { //비로그인 사용자
            postDetailDtoOptional = postQueryRepository.getPostDetail(postId);
        } else { //로그인한 사용자
            long memberId = tokenProvider.getMemberId(authentication);
            postDetailDtoOptional = postQueryRepository.getPostDetailWithLoginMember(postId, memberId);
        }

        PostDetailDto postDetailDto = postDetailDtoOptional
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (postDetailDto.isLiveChatActive()) {
            postDetailDto.setLiveChatUserCount(chatRoomRepository.getUserCount(String.valueOf(postId)));
        }

        return PostDetailRes.of(postDetailDto, attachmentRepository.findUrlsByPostId(postId));
    }

    public Slice<PostRes> getPosts(int page, int size, PostSort sort) {
        PageRequest pageRequest = PageRequest.of(page, size);
        if (sort == PostSort.HOT) {
            return postQueryRepository.getSliceByPopularity(pageRequest, sort.getType()).map(PostRes::of);
        } else {
            return postQueryRepository.getSlice(pageRequest, sort.getType()).map(PostRes::of);
        }
    }

    public Slice<PostRes> getMyPage(long memberId, int page, int size, MyPageSort sort) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Slice<PostSummaryDto> postSlice;

        switch (sort) {
            case POSTS : postSlice = postQueryRepository.getSliceByMemberId(pageRequest, memberId); break;
            case LIKES : postSlice = postQueryRepository.getSliceFromLikes(pageRequest, memberId); break;
            case CHOICES : postSlice = postQueryRepository.getSliceFromChoices(pageRequest, memberId); break;
            case COMMENTS : postSlice = postQueryRepository.getSliceFromComments(pageRequest, memberId); break;
            default: throw new IllegalStateException("Unexpected value: " + sort);
        }
        return postSlice.map(PostRes::of);
    }

    public Slice<PostRes> getPostsByTag(int page, int size, String tag) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return postQueryRepository.getSliceByTag(pageRequest, tag).map(PostRes::of);
    }

    public void deletePost(long memberId, long postId) {
        if (!postQueryRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        postQueryRepository.deletePost(postId);
    }

    public Post findByPostId(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND)
        );
    }
}
