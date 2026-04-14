# Микросервис авторизации
![GitHub Created At](https://img.shields.io/github/created-at/FantasmaDux/AuthMicro?color=bright-green&style=flat-square)
![Docker](https://img.shields.io/badge/docker-✓-blue?style=flat-square)
[![license](https://img.shields.io/github/license/FantasmaDux/AuthMicro.svg?color=bright-green&style=flat-square)](LICENSE)


Микросервис использует для авторизации одноразовые коды и почту. Реализован на JWT токенах. Микросервис является частью проекта чата.

Токены у себя хранит frontend.

## Содержание
- [Технологии](#технологии)
- [Безопасность](#безопасность)
- [Начало работы](#начало-работы)
- [Использование](#использование)
- [API](#api)
- [Лицензия](#лицензия)

## Технологии
- [Java 21](https://www.java.com/)
- [Spring Boot 3](https://spring.io/projects/spring-boot)
- [Spring Security 6](https://spring.io/projects/spring-security)
- [Docker](https://www.docker.com/)
- [PostgreSQL](https://www.postgresql.org/)
- [Gradle](https://gradle.org/)
- [JWT](https://jwt.io/)
- [gRPC](https://grpc.io/)

## Безопасность
- Аутентификация по одноразовым кодам через email
- JWT токены (access + refresh)
- Валидация входных данных

## Начало работы

### Запуск проекта
### Запуск серверной части приложения целиком (рекомендуется)
Перейти в chatStrucure репозиторий и запустить проект целиком.

Сервис будет доступен по адресу http://localhost/auth

### Запуск только сервиса авторизации
Для запуска только этого сервиса требуется замена данных в application.yml. 
``` bash
docker compose up --build
```
Сервис будет доступен по адресу http://localhost:8080

### Очистка

```bash
docker compose down --rmi all --volumes --remove-orphans
```

## Использование
Рекомендуется использовать Docker Desktop при работе на windows.
Рекомендуется использовать Postman для отладки и разработки.

Swagger доступен по адресу: http://localhost:8080/swagger-ui/index.html

## API
### Регистрация пользователя
API: /auth/v1/registration

input:
```
{
  "email": "user108@mail.ru",
  "acceptedPrivacyPolicy": true,
  "acceptedPersonalDataProcessing": true
}
```

output: 
```
{
    "codeExpires": 1762185347215,
    "codePattern": "[0-9]{6}"
}
```
На почту приходит код.

### Подтверждение регистрации пользователя
API: /auth/v1/registration/confirmEmail
input:
```
{
  "email": "user108@mail.ru",
  "code": "996437"
}
```

output:
```
{
    200 code
}
```

### Логин пользователя
API: /auth/v1/login/sendCodeEmail

input:
```
{
  "email": "user108@mail.ru"
}
```

output:
```
{
    "codeExpires": 1762185606647,
    "codePattern": "[0-9]{6}"
}
```
На почту приходит код.
### Подтверждение логина пользователя

API: /auth/v1/login/confirmEmail

input:
```
{
  "email": "user108@mail.ru",
  "code": "373528"
}
```

output:
```
{
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50SWQiOiI1ZGNmOTM3OC0yMjY3LTRjODEtOTJkNC0zMWJmODM0ODhkZmIiLCJ0eXBlIjoicmVmcmVzaCIsImlzcyI6Im1pY3JvLWF1dGgiLCJpYXQiOjE3NjIxODUzNzMsImV4cCI6MTc2MjYxNzM3M30.ZVdLQ_Y8fFK0dhoXE9lOIFDzhGGZMShowJhJqz_W_-o",
    "refreshTokenExpires": 1762617373000,
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50SWQiOiI1ZGNmOTM3OC0yMjY3LTRjODEtOTJkNC0zMWJmODM0ODhkZmIiLCJyb2xlcyI6WyJ1c2VyIl0sInR5cGUiOiJhY2Nlc3MiLCJpc3MiOiJtaWNyby1hdXRoIiwiaWF0IjoxNzYyMTg1MzczLCJleHAiOjE3NjIxODU2NzN9.og2CDzNNSYqMEJkrVX0btdDO_CACxfdZuLr1K0padfE",
    "accessTokenExpires": 1762185673000
}
```
### Обновление токенов пользователя
API: /auth/v1/refreshToken

input:
```
{
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50SWQiOiI1ZGNmOTM3OC0yMjY3LTRjODEtOTJkNC0zMWJmODM0ODhkZmIiLCJ0eXBlIjoicmVmcmVzaCIsImlzcyI6Im1pY3JvLWF1dGgiLCJpYXQiOjE3NjIxODUzNzMsImV4cCI6MTc2MjYxNzM3M30.ZVdLQ_Y8fFK0dhoXE9lOIFDzhGGZMShowJhJqz_W_-o"
}
```

output:
```
{
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50SWQiOiI1ZGNmOTM3OC0yMjY3LTRjODEtOTJkNC0zMWJmODM0ODhkZmIiLCJ0eXBlIjoicmVmcmVzaCIsImlzcyI6Im1pY3JvLWF1dGgiLCJpYXQiOjE3NjIxODU0MzksImV4cCI6MTc2MjYxNzQzOX0.MjgEJy2IUU4hsGsirM6R4ME452B-8Xc_ASdsH41uXIA",
    "refreshTokenExpires": 1762617439000,
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50SWQiOiI1ZGNmOTM3OC0yMjY3LTRjODEtOTJkNC0zMWJmODM0ODhkZmIiLCJyb2xlcyI6WyJ1c2VyIl0sInR5cGUiOiJhY2Nlc3MiLCJpc3MiOiJtaWNyby1hdXRoIiwiaWF0IjoxNzYyMTg1NDM5LCJleHAiOjE3NjIxODU3Mzl9.yXzh9D8W1JoOjDUaRrDrWRu_O_3pjmITzDt_3sHbVlQ",
    "accessTokenExpires": 1762185739000
}
```
## Скриншоты работы
При регистрации сообщение отправляется на почту (mail).

![Скриншот регистрации](docs/registration-email.png)

При регистрации сообщение отправляется на почту (gmail).
![Скриншот регистрации](docs/registration-gmail.png)

## Лицензия

[MIT](https://choosealicense.com/licenses/mit/)
