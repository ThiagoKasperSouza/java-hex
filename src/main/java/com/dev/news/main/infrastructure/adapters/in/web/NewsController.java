package com.dev.news.main.infrastructure.adapters.in.web;

import com.dev.news.main.application.usecases.news.CreateNewsUseCase;
import com.dev.news.main.application.usecases.news.DeleteNewsUseCase;
import com.dev.news.main.application.usecases.news.FindNewsByIdUseCase;
import com.dev.news.main.application.usecases.news.ListNewsUseCase;
import com.dev.news.main.domain.news.model.News;
import com.dev.news.main.infrastructure.adapters.in.web.dto.CreateNewsRequest;
import com.dev.news.main.infrastructure.adapters.in.web.dto.NewsResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final CreateNewsUseCase createNewsUseCase;
    private final FindNewsByIdUseCase findNewsByIdUseCase;
    private final ListNewsUseCase listNewsUseCase;
    private final DeleteNewsUseCase deleteNewsUseCase;

    public NewsController(
            CreateNewsUseCase createNewsUseCase,
            FindNewsByIdUseCase findNewsByIdUseCase,
            ListNewsUseCase listNewsUseCase,
            DeleteNewsUseCase deleteNewsUseCase
    ) {
        this.createNewsUseCase = createNewsUseCase;
        this.findNewsByIdUseCase = findNewsByIdUseCase;
        this.listNewsUseCase = listNewsUseCase;
        this.deleteNewsUseCase = deleteNewsUseCase;
    }

    @GetMapping
    public ResponseEntity<List<NewsResponse>> listAll() {
        List<NewsResponse> noticias = listNewsUseCase.execute().stream()
                .map(NewsResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(noticias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponse> findById(@PathVariable Long id) {
        News news = findNewsByIdUseCase.execute(id);
        return ResponseEntity.ok(NewsResponse.fromDomain(news));
    }

    @PostMapping
    public ResponseEntity<NewsResponse> create(@RequestBody CreateNewsRequest request) {
        News criada = createNewsUseCase.execute(request.title(), request.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(NewsResponse.fromDomain(criada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteNewsUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
