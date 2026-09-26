# News API

API REST de notícias construída com arquitetura **hexagonal** (Ports & Adapters) em **Java 17+** com **Spring Boot 4** e **PostgreSQL**.

O projeto segue o padrão:

```
src/main/java/com/dev/news/main/
├── application/usecases/       # Casos de uso / regras de aplicação
├── domain/                     # Núcleo: modelo e regras de negócio
│   ├── news/model/News.java    # record com validações (título/conteúdo obrigatórios)
│   ├── news/ports/NewsRepository.java  # Porta de saída
│   └── news/exceptions/
└── infrastructure/             # Adaptadores (web in / persistence out)
    ├── adapters/in/web/        # Controller + DTOs + tratamento de exceções
    └── adapters/out/persistence/  # JPA/Spring Data + adapter
```

---

## ⚙️ Pré-requisitos

- **Docker** com o daemon em execução (para subir toda a stack):
  ```bash
  sudo systemctl start docker   # inicia o daemon (se ainda não estiver)
  docker info                   # confirma que o daemon está no ar
  ```
- **Java 25 + Gradle** (opcional — apenas para desenvolvimento local sem Docker).

---

## Como importar o arquivo News-API-Insomnia.json no Insomnia
Abra o Insomnia.
Menu Application → Preferences → Data (ou Ctrl+Shift+, → guia Data).
Clique em Import Data → From File.
Selecione News-API-Insomnia.json.
Pronto: aparecerá o workspace News API com os 4 requests prontos.

## 🚀 Subindo com Docker Compose

Todos os arquivos necessários já estão no projeto: `Dockerfile`, `.dockerignore`, `docker-compose.yml` e `.env`.

### 1) Subir a stack (PostgreSQL + API)

```bash
cd /local/do/projeto
docker compose up -d --build
```

Isso vai:
1. Construir a imagem da API (multi-estágio: build com JDK 25 + runtime JRE 25).
2. Subir o `postgres:16-alpine`.
3. Subir a API em `http://localhost:8080`.

### 2) Verificar o status

```bash
docker compose ps
```

Saída esperada (o `postgres` deve ficar como `healthy` e a `news_api` como `Up`):

```
NAME               STATUS                    PORTS
news_api           Up ...                    0.0.0.0:8080->8080/tcp
postgres_db_news   Up ... (healthy)          0.0.0.0:5432->5432/tcp
```

### 3) Ver os logs

```bash
docker compose logs -f app        # logs da API
docker compose logs -f postgres   # logs do banco
```

### 4) Parar / apagar

```bash
docker compose down        # para os containers (mantém o volume de dados)
docker compose down -v     # para E apaga o volume (dados zerados)
```

---

## 🗄️ Popular o banco de dados

### Automático (recomendado)

O arquivo `src/main/resources/data.sql` **cria a tabela e insere 100 notícias automaticamente** na primeira subida do `postgres` (volume vazio). Ele é **idempotente**: em reinícios, não recria a tabela nem duplica os dados.

Ou seja, ao fazer `docker compose up -d --build` com um volume novo, o banco já sobe populado. Para conferir:

```bash
docker exec postgres_db_news psql -U postgres -d db_news -c "SELECT count(*) FROM news;"
# count = 100
```

### Manual via `psql`

Você pode rodar o mesmo script manualmente (por exemplo, após `docker compose down -v` para "zerar" e re-popular):

```bash
docker exec -i postgres_db_news psql -U postgres -d db_news < src/main/resources/data.sql
```

Ou direto dentro do container:

```bash
docker exec -it postgres_db_news psql -U postgres -d db_news
```

> **Importante:** o `data.sql` é a fonte da verdade da estrutura (DDL + seed). As variáveis de conexão vêm do `.env` (`POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_PORT`).

### Resetar os dados

Para recomeçar do zero (apagar o volume e re-sembrar automaticamente na próxima subida):

```bash
cd /local/do/projeto
docker compose down -v
docker compose up -d --build
```

---

## 🔌 Consumir a API

Base URL: `http://localhost:8080`

| Método | Endpoint | Descrição | Código esperado |
|--------|----------|-----------|-----------------|
| `GET` | `/api/news` | Lista todas as notícias | `200 OK` |
| `GET` | `/api/news/{id}` | Busca uma notícia por ID | `200` / `404` |
| `POST` | `/api/news` | Cria uma notícia | `201 Created` / `400` |
| `DELETE` | `/api/news/{id}` | Remove uma notícia | `204 No Content` / `404` |

### Listar todas

```bash
curl http://localhost:8080/api/news
```

```json
[
  {
    "id": 1,
    "title": "Inteligência Artificial revoluciona diagnóstico médico",
    "content": "Novos modelos de IA estão auxiliando médicos a identificar doenças raras com maior precisão.",
    "createdAt": "2026-09-25T..."
  }
]
```

### Buscar por ID

```bash
curl http://localhost:8080/api/news/1
```

Se o ID não existir:

```bash
curl -i http://localhost:8080/api/news/99999
# HTTP/1.1 404
# {"detail":"Notícia não encontrada com o ID: 99999","status":404,"title":"Not Found"}
```

### Criar uma notícia

```bash
curl -X POST http://localhost:8080/api/news \
  -H "Content-Type: application/json" \
  -d '{"title":"Minha primeira notícia","content":"Conteúdo da minha notícia."}'
```

Resposta (`201 Created`):

```json
{
  "id": 101,
  "title": "Minha primeira notícia",
  "content": "Conteúdo da minha notícia.",
  "createdAt": "2026-09-26T..."
}
```

**Regra de negócio:** título e conteúdo são obrigatórios. Enviar valores vazios retorna `400`:

```bash
curl -i -X POST http://localhost:8080/api/news \
  -H "Content-Type: application/json" \
  -d '{"title":"","content":"x"}'
# HTTP/1.1 400
# {"detail":"O título da notícia não pode ser vazio.","status":400,"title":"Bad Request"}
```

### Deletar uma notícia

```bash
curl -i -X DELETE http://localhost:8080/api/news/101
# HTTP/1.1 204 No Content
```

Deletar um ID inexistente:

```bash
curl -i -X DELETE http://localhost:8080/api/news/99999
# HTTP/1.1 404
```

---

## 🛠️ Rodar em desenvolvimento (sem Docker)

> Exige Java 25 e um PostgreSQL disponível (na porta padrão `5432`, com as credenciais do `.env`).

```bash
cd /home/thiag/java-hex
export JAVA_HOME=/caminho/para/jdk-25   # opcional, se o JDK não for o padrão do PATH
bash ./gradlew bootRun
```

Por padrão, o app conecta em `jdbc:postgresql://localhost:5432/db_news`. Para apontar para outro banco/porta, use variáveis de ambiente:

```bash
export DB_HOST=localhost POSTGRES_PORT=5432 POSTGRES_DB=db_news POSTGRES_USER=postgres POSTGRES_PASSWORD=sua_senha
bash ./gradlew bootRun
```

---

## 🧪 Rodar os testes

Os testes são executados com o **JDK 25** e o **Gradle wrapper**. O `gradlew` precisa ter permissão de execução — se necessário, rode `chmod +x gradlew` uma vez.

> ⚠️ **Não use `sudo`** para rodar o Gradle. Execute sempre como seu usuário normal (você já está no grupo `docker`).

### Executar todos os testes

```bash
cd /home/thiag/java-hex
export JAVA_HOME=/caminho/para/jdk-25   # se o JDK 25 não for o padrão do PATH
./gradlew test
```

> ⚠️ O teste de integração usa **Testcontainers**, que sobe um PostgreSQL real. Portanto, o **daemon do Docker precisa estar em execução** — confirme com `docker info`.

### Estrutura dos testes

| Camada | Classe | O que cobre |
|--------|--------|-------------|
| Domínio | `NewsTest` | Regras de negócio: título e conteúdo obrigatórios. |
| Casos de uso | `CreateNewsUseCaseTest` · `FindNewsByIdUseCaseTest` · `ListNewsUseCaseTest` · `DeleteNewsUseCaseTest` | Lógica dos casos de uso com repositório mockado (Mockito). |
| Controller | `NewsControllerTest` | Endpoints HTTP (200/201/204/400/404) via MockMvc. |
| Integração | `MainApplicationTests` | Ponta a ponta com PostgreSQL real (Testcontainers) + `data.sql`. |

### Rodar um teste específico

```bash
./gradlew test --tests "NewsControllerTest"
./gradlew test --tests "MainApplicationTests"
```

Após a execução, o relatório detalhado fica em `build/reports/tests/test/index.html`.

---

## 📁 Configuração (`.env`)

As variáveis de ambiente usadas pelo compose e pela aplicação estão em `.env`:

```
POSTGRES_DB=db_news
POSTGRES_USER=postgres
POSTGRES_PASSWORD=...
POSTGRES_PORT=5432
```
