package com.winnow.bestchoice.model.dto;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomPage<T> {

    private final List<T> chatRooms;
    private final int pageSize;

    public ChatRoomPage(List<T> chatRooms, int pageSize) {
        this.chatRooms = chatRooms;
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) chatRooms.size() / pageSize);
    }

    public List<T> getPage(int pageNumber) {
        if (pageNumber < 1 || pageNumber > getTotalPages()) {
            throw new IllegalArgumentException("유효하지 않은 페이지 번호입니다.");
        }

        int startIndex = (pageNumber -1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, chatRooms.size());

        return new ArrayList<>(chatRooms.subList(startIndex, endIndex));
    }
}
