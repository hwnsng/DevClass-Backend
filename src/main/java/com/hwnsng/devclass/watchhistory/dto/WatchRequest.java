package com.hwnsng.devclass.watchhistory.dto;

import lombok.Getter;

@Getter
public class WatchRequest {
    private Long userId;
    private Long courseId;
    private int positionSeconds; // 현재 재생 위치 (초)
}
