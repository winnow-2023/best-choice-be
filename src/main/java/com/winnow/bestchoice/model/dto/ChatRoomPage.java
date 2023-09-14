package com.winnow.bestchoice.model.dto;

import java.util.List;

public class ChatRoomPage<T> {

    private List<T> chatrooms;
    private int totalCount;      // 전체 갯수
    private int size;            // 페이지당 가져올 갯수
    private int currentPage;     // 현재 페이지

    public ChatRoomPage(List<T> data, int page, int size) {
        this.chatrooms = data;
        this.totalCount = data.size();
        this.size = size;
        setCurrentPage(page);
    }

    public void setCurrentPage(int page) {
        if (page <= 0) {
            this.currentPage = 1;
        } else if (page > getTotalPages()) {
            this.currentPage = getTotalPages();
        } else {
            this.currentPage = page;
        }
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) totalCount / size);
    }

    public List<T> getPagedData() {
        int start = (currentPage - 1) * size;
        int end = Math.min(start + size, totalCount);
        return chatrooms.subList(start, end);
    }
}
