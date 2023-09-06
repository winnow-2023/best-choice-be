package com.winnow.bestchoice.type;

import com.querydsl.core.types.OrderSpecifier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.winnow.bestchoice.entity.QComment.comment;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum CommentSort {
    LATEST("createdDate", comment.createdDate.asc()),
    LIKES("likeCount", comment.likeCount.desc());

    private String value;
    private OrderSpecifier<?> type;
}
