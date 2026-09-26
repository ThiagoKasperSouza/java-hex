package com.dev.news.main.infrastructure.adapters.in.web.dto;

public record UpdateNewsRequest(
        String title,
        String content
) {}
