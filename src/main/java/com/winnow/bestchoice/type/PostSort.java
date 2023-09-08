package com.winnow.bestchoice.type;

import com.querydsl.core.types.OrderSpecifier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.winnow.bestchoice.entity.QPost.post;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum PostSort {
    LATEST("createdDate", post.createdDate.desc()),
    LIKES("likeCount", post.likeCount.desc()),
    COMMENTS("commentCount", post.commentCount.desc()),
    HOT("popularityDate", post.popularityDate.desc());

    private String value;
    private OrderSpecifier<?> type;
}
