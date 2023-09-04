package com.winnow.bestchoice.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.winnow.bestchoice.entity.QPostTag;
import com.winnow.bestchoice.entity.QTag;
import com.winnow.bestchoice.model.dto.PostDetailDto;
import com.winnow.bestchoice.model.dto.PostSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.winnow.bestchoice.entity.QChoice.choice;
import static com.winnow.bestchoice.entity.QComment.comment;
import static com.winnow.bestchoice.entity.QMember.member;
import static com.winnow.bestchoice.entity.QPost.post;
import static com.winnow.bestchoice.entity.QPostLike.postLike;
import static com.winnow.bestchoice.entity.QPostTag.postTag;
import static com.winnow.bestchoice.entity.QTag.tag;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public Slice<PostSummaryDto> getSlice(Pageable pageable, OrderSpecifier<?> type) {//popularityDate null 제외 조회 처리
        List<PostSummaryDto> content = getPostSummaryDtosQuery()
                .orderBy(type)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return getSlice(pageable, content);
    }

    public Optional<PostDetailDto> getPostDetail(long postId, long memberId) {
        return Optional.ofNullable(jpaQueryFactory.select(Projections.bean(PostDetailDto.class,
                        post.id, member.id.as("memberId"), member.nickname
                        , choice.choices.as("myChoice"), postLike.isNotNull().as("liked")
                        , post.title, post.content, post.optionA, post.optionB, post.tags
                        , post.createdDate, post.popularityDate, post.likeCount
                        , post.ACount, post.BCount, post.commentCount))
                .from(post).join(post.member, member)
                .leftJoin(choice).on(choice.post.eq(post), choice.member.id.eq(memberId))
                .leftJoin(postLike).on(postLike.post.eq(post), postLike.member.id.eq(memberId))
                .where(post.id.eq(postId))
                .fetchOne());
    }

    public Slice<PostSummaryDto> getSliceByMemberId(Pageable pageable, long memberId) {
        List<PostSummaryDto> content = getPostSummaryDtosQuery()
                .where(member.id.eq(memberId))
                .orderBy(post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return getSlice(pageable, content);
    }

    public Slice<PostSummaryDto> getSliceFromLikes(Pageable pageable, long memberId) {
        List<PostSummaryDto> content = getPostSummaryDtosQuery()
                .from(post).join(post.member, member)
                .join(postLike).on(postLike.post.eq(post))
                .where(postLike.member.id.eq(memberId))
                .orderBy(postLike.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return getSlice(pageable, content);
    }

    public Slice<PostSummaryDto> getSliceFromComments(Pageable pageable, long memberId) {// distinct 필요 (중복 조회)
        List<PostSummaryDto> content = getPostSummaryDtosQuery()
                .join(comment).on(comment.post.eq(post))
                .where(comment.member.id.eq(memberId))
                .orderBy(comment.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return getSlice(pageable, content);
    }

    public Slice<PostSummaryDto> getSliceFromChoices(Pageable pageable, long memberId) {
        List<PostSummaryDto> content = getPostSummaryDtosQuery()
                .join(choice).on(choice.post.eq(post))
                .where(choice.member.id.eq(memberId))
                .orderBy(choice.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return getSlice(pageable, content);
    }

    public Slice<PostSummaryDto> getSliceByTag(Pageable pageable, String tagName) {
        List<PostSummaryDto> content = getPostSummaryDtosQuery()
                .join(postTag).on(postTag.post.eq(post))
                .join(tag).on(tag.name.eq(tagName), postTag.tag.eq(tag))
                .orderBy(postTag.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return getSlice(pageable, content);
    }

    private SliceImpl<PostSummaryDto> getSlice(Pageable pageable, List<PostSummaryDto> content) {
        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private JPAQuery<PostSummaryDto> getPostSummaryDtosQuery() {
        return jpaQueryFactory.select(Projections.bean(PostSummaryDto.class,
                        post.id, member.id.as("memberId"), member.nickname
                        , post.title, post.optionA, post.optionB, post.tags
                        , post.createdDate, post.popularityDate, post.likeCount
                        , post.ACount, post.BCount, post.commentCount))
                .from(post).join(post.member, member);
    }
}
