package com.dev.news.main;

import com.dev.news.main.application.usecases.news.CreateNewsUseCase;
import com.dev.news.main.application.usecases.news.DeleteNewsUseCase;
import com.dev.news.main.application.usecases.news.FindNewsByIdUseCase;
import com.dev.news.main.application.usecases.news.ListNewsUseCase;
import com.dev.news.main.domain.news.ports.NewsRepository;
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
}
