package com.dev.news.main;

import com.dev.news.main.application.usecases.news.CreateNewsUseCase;
import com.dev.news.main.application.usecases.news.DeleteNewsUseCase;
import com.dev.news.main.application.usecases.news.FindNewsByIdUseCase;
import com.dev.news.main.application.usecases.news.ListNewsUseCase;
import com.dev.news.main.application.usecases.news.UpdateNewsUseCase;
import com.dev.news.main.application.usecases.user.CreateUserUseCase;
import com.dev.news.main.application.usecases.user.LoginUseCase;
import com.dev.news.main.domain.news.ports.NewsRepository;
import com.dev.news.main.domain.user.ports.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    public CreateNewsUseCase createNewsUseCase(NewsRepository repository) {
        return new CreateNewsUseCase(repository);
    }

    @Bean
    public FindNewsByIdUseCase findNewsByIdUseCase(NewsRepository repository) {
        return new FindNewsByIdUseCase(repository);
    }

    @Bean
    public ListNewsUseCase listNewsUseCase(NewsRepository repository) {
        return new ListNewsUseCase(repository);
    }

    @Bean
    public DeleteNewsUseCase deleteNewsUseCase(NewsRepository repository) {
        return new DeleteNewsUseCase(repository);
    }

    @Bean
    public UpdateNewsUseCase updateNewsUseCase(NewsRepository repository) {
        return new UpdateNewsUseCase(repository);
    }

    @Bean
    public CreateUserUseCase createUserUseCase(UserRepository repository, PasswordEncoder passwordEncoder) {
        return new CreateUserUseCase(repository, passwordEncoder);
    }

    @Bean
    public LoginUseCase loginUseCase(UserRepository repository, PasswordEncoder passwordEncoder) {
        return new LoginUseCase(repository, passwordEncoder);
    }
}
