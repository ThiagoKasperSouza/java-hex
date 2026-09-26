package com.dev.news.main.infrastructure.adapters.in.web;

import com.dev.news.main.application.usecases.user.CreateUserUseCase;
import com.dev.news.main.application.usecases.user.LoginUseCase;
import com.dev.news.main.domain.user.model.User;
import com.dev.news.main.infrastructure.adapters.in.web.dto.CreateUserRequest;
import com.dev.news.main.infrastructure.adapters.in.web.dto.LoginRequest;
import com.dev.news.main.infrastructure.adapters.in.web.dto.LoginResponse;
import com.dev.news.main.infrastructure.adapters.in.web.dto.UserResponse;
import com.dev.news.main.infrastructure.security.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final LoginUseCase loginUseCase;
    private final JwtService jwtService;

    public UserController(CreateUserUseCase createUserUseCase,
                          LoginUseCase loginUseCase,
                          JwtService jwtService) {
        this.createUserUseCase = createUserUseCase;
        this.loginUseCase = loginUseCase;
        this.jwtService = jwtService;
    }

    /** Cadastro público — sempre cria um usuário com papel USER. */
    @PostMapping("/user")
    public ResponseEntity<UserResponse> register(@RequestBody CreateUserRequest request) {
        User user = createUserUseCase.execute(request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromDomain(user));
    }

    /** Login — valida credenciais e define um cookie httpOnly com o JWT. */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = loginUseCase.execute(request.username(), request.password());
        String token = jwtService.generateToken(user);

        ResponseCookie cookie = ResponseCookie.from("news_token", token)
                .httpOnly(true)
                .secure(false) // localhost/http; trocar para true em produção (HTTPS)
                .path("/")
                .maxAge(Duration.ofHours(jwtService.expirationHours()))
                .sameSite("Lax")
                .build();

        LoginResponse response = new LoginResponse(user.username(), user.role(), token);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }
}
