package com.dev.news.main.infrastructure.adapters.in.web.dto;

import com.dev.news.main.domain.news.model.News;
import java.time.LocalDateTime;

public record NewsResponse(
    Long id,
    String title,
    String content,
    LocalDateTime createdAt
) {
    public static NewsResponse fromDomain(News news) {
        return new NewsResponse(
            news.id(),
            news.title(),
            news.content(),
            news.createdAt()
        );
    }
}
