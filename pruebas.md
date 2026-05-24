# Evidencia de Pruebas Locales - GameHub Store

## 1. Entorno de Base de Datos (Laragon + HeidiSQL)
* **Estado:** Exitoso.
* **Detalle:** El microservicio `auth-service` se conectó correctamente al motor MySQL en el puerto `3306`. Se comprobó que Hibernate generó la base de datos `gamehub_auth` y la tabla `usuarios` con sus respectivas columnas (`id`, `nombre`, `password`, `username`).

## 2. Pruebas de Endpoints con Postman
* **Endpoint Probado:** `POST http://localhost:8081/api/auth/register`
* **Cuerpo de la Petición (JSON):**
```json
{ 
  "username": "christian.gamer",
  "password": "password123",
  "nombre": "Christian"
} 