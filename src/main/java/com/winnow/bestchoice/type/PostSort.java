package com.winnow.bestchoice.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum PostSort {
    LATEST("createdDate"), LIKES("likeCount"), HOT("popularityDate");

    private String value;
}
