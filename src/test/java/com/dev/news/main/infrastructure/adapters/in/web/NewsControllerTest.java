package com.dev.news.main.infrastructure.adapters.in.web;

import com.dev.news.main.application.usecases.news.CreateNewsUseCase;
import com.dev.news.main.application.usecases.news.DeleteNewsUseCase;
import com.dev.news.main.application.usecases.news.FindNewsByIdUseCase;
import com.dev.news.main.application.usecases.news.ListNewsUseCase;
import com.dev.news.main.application.usecases.news.UpdateNewsUseCase;
import com.dev.news.main.domain.news.exceptions.NewsNotFoundException;
import com.dev.news.main.domain.news.model.News;
import com.dev.news.main.infrastructure.adapters.in.web.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class NewsControllerTest {

    private CreateNewsUseCase createNewsUseCase;
    private FindNewsByIdUseCase findNewsByIdUseCase;
    private ListNewsUseCase listNewsUseCase;
    private DeleteNewsUseCase deleteNewsUseCase;
    private UpdateNewsUseCase updateNewsUseCase;
    private MockMvc mockMvc;

    private static final News NEWS = new News(1L, "Titulo", "Conteudo", LocalDateTime.now());

    @BeforeEach
    void setUp() {
        createNewsUseCase = mock(CreateNewsUseCase.class);
        findNewsByIdUseCase = mock(FindNewsByIdUseCase.class);
        listNewsUseCase = mock(ListNewsUseCase.class);
        deleteNewsUseCase = mock(DeleteNewsUseCase.class);
        updateNewsUseCase = mock(UpdateNewsUseCase.class);

        NewsController controller = new NewsController(
                createNewsUseCase, findNewsByIdUseCase, listNewsUseCase, deleteNewsUseCase, updateNewsUseCase);

        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldListAllNews() throws Exception {
        when(listNewsUseCase.execute()).thenReturn(List.of(NEWS));

        mockMvc.perform(get("/api/news"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Titulo"))
                .andExpect(jsonPath("$[0].content").value("Conteudo"));
    }

    @Test
    void shouldFindById() throws Exception {
        when(findNewsByIdUseCase.execute(1L)).thenReturn(NEWS);

        mockMvc.perform(get("/api/news/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldReturn404WhenNotFound() throws Exception {
        when(findNewsByIdUseCase.execute(99L)).thenThrow(new NewsNotFoundException(99L));

        mockMvc.perform(get("/api/news/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Notícia não encontrada com o ID: 99"));
    }

    @Test
    void shouldCreateNews() throws Exception {
        when(createNewsUseCase.execute("Titulo", "Conteudo"))
                .thenReturn(new News(10L, "Titulo", "Conteudo", LocalDateTime.now()));

        mockMvc.perform(post("/api/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Titulo\",\"content\":\"Conteudo\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Titulo"));
    }

    @Test
    void shouldReturn400ForInvalidNews() throws Exception {
        when(createNewsUseCase.execute(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("O título da notícia não pode ser vazio."));

        mockMvc.perform(post("/api/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"content\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("O título da notícia não pode ser vazio."));
    }

    @Test
    void shouldUpdateNews() throws Exception {
        when(updateNewsUseCase.execute(1L, "NovoTitulo", "NovoConteudo"))
                .thenReturn(new News(1L, "NovoTitulo", "NovoConteudo", LocalDateTime.now()));

        mockMvc.perform(put("/api/news/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"NovoTitulo\",\"content\":\"NovoConteudo\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("NovoTitulo"));
    }

    @Test
    void shouldDeleteNews() throws Exception {
        mockMvc.perform(delete("/api/news/1"))
                .andExpect(status().isNoContent());

        verify(deleteNewsUseCase).execute(1L);
    }
}
