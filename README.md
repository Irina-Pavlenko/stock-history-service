# Stock History Service
REST сервис для получения и хранения исторических данных о ценах на акции.
Технологии
Java 17, Spring Boot 3.5.8
Spring Security + JWT
PostgreSQL + Spring Data JPA
Polygon.io API
Liquibase, Docker

API Endpoints

POST	/api/user/register	Регистрация
POST	/api/user/login	Логин (получение JWT)
POST	/api/user/save	Сохранение данных акций
GET	    /api/user/saved	Получение сохраненных данных