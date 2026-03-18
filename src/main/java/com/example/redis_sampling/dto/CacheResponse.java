package com.example.redis_sampling.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CacheResponse<T> {
    private final T data;
    private final String source; // "CACHE" 또는 "DATABASE"
    private final long elapsedMillis;
}
