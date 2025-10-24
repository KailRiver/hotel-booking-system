# Hotel Booking System - Microservices Architecture
## Компоненты системы
* Eureka Server (8761) - Service Discovery
* API Gateway (8080) - Единая точка входа, маршрутизация
* Booking Service (8081) - Управление бронированиями, пользователями, аутентификация
* Hotel Service (8082) - Управление отелями и номерами

## Возможности

* Регистрация и вход пользователей (JWT) через Booking Service
* Создание бронирований с двухшаговой согласованностью (PENDING → CONFIRMED/CANCELLED с компенсацией)
* Идемпотентность запросов с correlationId
* Подсказки по выбору номера (сортировка по timesBooked, затем по id)
* Администрирование пользователей (CRUD) и отелей/номеров (CRUD) для админов
* Агрегации: популярность номеров по timesBooked
* Сквозная корреляция с заголовком X-Correlation-Id

## Архитектура и порты

* eureka-server: порт 8761
* api-gateway: порт 8080
* hotel-service: порт 8082, регистрируется в Eureka под именем hotel-service
* booking-service: порт 8081, регистрируется в Eureka под именем booking-service

Gateway маршрутизирует запросы к сервисам по их serviceId через Eureka и проксирует заголовок Authorization (JWT).

## Требования

* Java 17+
* Maven 3.6+

## Сборка и запуск

Запустить Eureka:

```
mvn -pl eureka-server spring-boot:run
```
Запустить API Gateway:

```
mvn -pl api-gateway spring-boot:run
```
Запустить Hotel Service и Booking Service (в отдельных терминалах):

```
mvn -pl hotel-service spring-boot:run
mvn -pl booking-service spring-boot:run
```
Совет: можно запустить все модули в отдельных окнах. После старта сервисы зарегистрируются в Eureka (http://localhost:8761).

Быстрый сценарий (через Gateway на 8080)

Регистрация пользователя

```
curl -X POST http://localhost:8080/user/register \
-H 'Content-Type: application/json' \
-d '{"username":"user1","password":"password","role":"USER"}'
```
Вход и получение JWT

```
TOKEN=$(curl -s -X POST http://localhost:8080/user/auth \
-H 'Content-Type: application/json' \
-d '{"username":"user1","password":"password"}' | grep -o '"token":"[^"]*' | cut -d'"' -f4)
```
Создание отеля и номера (нужны права admin):

```
# Создать отель
curl -X POST http://localhost:8080/api/hotels \
-H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
-d '{"name":"Hotel A","address":"Red Square, 1"}'

# Создать комнату
curl -X POST http://localhost:8080/api/rooms \
-H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
-d '{"hotelId":1,"number":"101","available":true,"timesBooked":0}'
```
Подсказки по номерам

```
curl "http://localhost:8080/api/rooms/recommend?startDate=2024-01-15T14:00:00&endDate=2024-01-17T12:00:00" \
-H "Authorization: Bearer $TOKEN"
```
Создание бронирования

```
curl -X POST http://localhost:8080/booking \
-H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
-d '{"roomId":1, "startDate":"2024-01-15T14:00:00", "endDate":"2024-01-17T12:00:00", "autoSelect":false}'
```
История бронирований пользователя

```
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/booking
```
## Основные эндпойнты

Через Gateway (8080):

Аутентификация (Booking Service):

* POST /user/register — регистрация (для админа указать "role": "ADMIN")
* POST /user/auth — получение JWT

Бронирования (Booking Service):

* GET /booking — мои бронирования
* POST /booking — создать бронирование (PENDING → CONFIRMED/компенсация)
* DELETE /booking/{id} — отменить бронирование

Пользователи (Booking Service, admin):

* GET /user — все пользователи
* POST /user — создать пользователя
* PATCH /user/{id} — обновить пользователя
* DELETE /user/{id} — удалить пользователя

Отели и номера (Hotel Service):

* GET /api/hotels — список отелей
* POST /api/hotels — создать отель (админ)
* GET /api/rooms — доступные номера на даты
* GET /api/rooms/recommend — рекомендованные номера (сортировка по загрузке)
* POST /api/rooms — создать номер (админ)

Внутренние эндпоинты (Hotel Service):

* POST /api/rooms/{id}/confirm-availability — удержание слота (идемпотентно по correlationId)
* POST /api/rooms/{id}/release — освобождение удержания (компенсация)
* POST /api/rooms/{id}/confirm-booking — подтверждение удержания

## Консоль H2

Включена для сервисов:

* Booking Service: http://localhost:8081/h2-console
* Hotel Service: http://localhost:8082/h2-console

JDBC URL: jdbc:h2:mem:[servicedb]
User: sa, Password: password