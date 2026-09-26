package com.dev.news.main.infrastructure.adapters.in.web.dto;

import com.dev.news.main.domain.user.model.Role;

/**
 * Retorno do login. O campo {@code token} é incluído no corpo para facilitar
 * o consumo (ex.: Bearer token), além do cookie httpOnly definido no header.
 */
public record LoginResponse(
        String username,
        Role role,
        String token
) {}
