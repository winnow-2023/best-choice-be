package com.winnow.bestchoice.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum CommentSort {
    LATEST("createdDate"), LIKES("likeCount");

    private String value;
}
