package com.hwnsng.devclass.bookmark.dto;

import lombok.Getter;

@Getter
public class BookmarkRequest {
    private Long userId;
    private int positionSeconds;
    private String note;
}
