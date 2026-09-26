package com.dev.news.main.infrastructure.adapters.in.web.dto;

public record CreateUserRequest(
        String username,
        String password
) {}
