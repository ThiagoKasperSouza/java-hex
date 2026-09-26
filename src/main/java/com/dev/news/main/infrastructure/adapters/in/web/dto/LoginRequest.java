package com.dev.news.main.infrastructure.adapters.in.web.dto;

public record LoginRequest(
        String username,
        String password
) {}
