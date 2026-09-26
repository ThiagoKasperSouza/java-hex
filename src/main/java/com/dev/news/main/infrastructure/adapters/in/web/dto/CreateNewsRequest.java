package com.dev.news.main.infrastructure.adapters.in.web.dto;

public record CreateNewsRequest(
    String title,
    String content
) {}
