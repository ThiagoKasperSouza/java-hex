# ---------- ESTÁGIO 1: build ----------
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app

# Wrapper do Gradle e arquivos de build
# (gradle.properties não é copiado: é específico da máquina e vai no .dockerignore)
COPY gradlew ./
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

# Código-fonte
COPY src src

# Gera o JAR executável (download de dependências + compilação)
RUN ./gradlew bootJar --no-daemon

# ---------- ESTÁGIO 2: runtime ----------
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
