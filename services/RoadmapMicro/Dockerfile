# ============================================
# Стадия сборки
# ============================================
FROM golang:1.24-alpine AS builder

# Устанавливаем необходимые утилиты
RUN apk add --no-cache git ca-certificates tzdata

WORKDIR /build

# Копируем go.mod и go.sum для кэширования зависимостей
COPY go.mod go.sum ./
RUN go mod download

# Копируем весь исходный код
COPY . .

# Собираем статический бинарник
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build \
    -ldflags="-w -s" \
    -o app ./cmd/roadmap/main.go   # путь к main.go, проверь у себя

# ============================================
# Финальный контейнер
# ============================================
FROM alpine:3.19

RUN apk add --no-cache ca-certificates tzdata

WORKDIR /app

# Копируем бинарник из стадии сборки
COPY --from=builder /build/app .

# Создаём непривилегированного пользователя
RUN adduser -D -u 1000 appuser && chown -R appuser:appuser /app
USER appuser

EXPOSE 8082

ENTRYPOINT ["./app"]