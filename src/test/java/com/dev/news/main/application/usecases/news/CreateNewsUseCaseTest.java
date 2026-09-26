package com.dev.news.main.application.usecases.news;

import com.dev.news.main.domain.news.model.News;
import com.dev.news.main.domain.news.ports.NewsRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateNewsUseCaseTest {

    private final NewsRepository repository = mock(NewsRepository.class);
    private final CreateNewsUseCase useCase = new CreateNewsUseCase(repository);

    @Test
    void shouldCreateNewsWithTimestampAndNullId() {
        when(repository.save(any(News.class))).thenAnswer(inv -> inv.getArgument(0));

        News result = useCase.execute("Titulo", "Conteudo");

        assertNull(result.id());
        assertEquals("Titulo", result.title());
        assertEquals("Conteudo", result.content());
        assertNotNull(result.createdAt());
    }

    @Test
    void shouldPersistNewsThroughRepository() {
        when(repository.save(any(News.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute("Titulo", "Conteudo");

        verify(repository).save(argThat(n ->
                n.title().equals("Titulo") && n.content().equals("Conteudo")));
    }
}
