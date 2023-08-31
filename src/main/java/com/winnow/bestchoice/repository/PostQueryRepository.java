package com.winnow.bestchoice.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.winnow.bestchoice.model.dto.PostSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.winnow.bestchoice.entity.QMember.member;
import static com.winnow.bestchoice.entity.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public Slice<PostSummaryDto> getSlice(Pageable pageable, OrderSpecifier<?> type) {
        List<PostSummaryDto> content = jpaQueryFactory.select(Projections.bean(PostSummaryDto.class,
                        post.id, member.id.as("memberId"), member.nickname
                        , post.title, post.optionA, post.optionB, post.tags
                        , post.createdDate, post.popularityDate, post.likeCount
                        , post.ACount, post.BCount, post.commentCount))
                .from(post).join(post.member, member)
                .orderBy(type)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
