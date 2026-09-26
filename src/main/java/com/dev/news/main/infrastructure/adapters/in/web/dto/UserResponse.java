package com.dev.news.main.infrastructure.adapters.in.web.dto;

import com.dev.news.main.domain.user.model.Role;
import com.dev.news.main.domain.user.model.User;

public record UserResponse(
        Long id,
        String username,
        Role role
) {
    public static UserResponse fromDomain(User user) {
        return new UserResponse(user.id(), user.username(), user.role());
    }
}
