# FitChain - Sistema de Gestión de Gimnasio

Sistema de gestión integral para cadena de gimnasios, desarrollado con arquitectura de microservicios usando Spring Boot.

## Integrantes

- Matias Rodriguez
- Cristofer Cifuentes

## Repositorio

https://github.com/FitChainn

---

## Descripción

FitChain es una plataforma backend distribuida que gestiona todas las operaciones de una cadena de gimnasios: clientes, entrenadores, establecimientos, membresías, pagos, reservas, asistencias, horarios, equipos y notificaciones. El sistema incluye autenticación JWT y un API Gateway centralizado.

---

## Microservicios

| Servicio         | Puerto | Base de Datos                  |
|------------------|--------|-------------------------------|
| Cliente          | 8081   | db_fitchain_clientes          |
| Entrenador       | 8082   | db_fitchain_entrenadores      |
| Equipo           | 8083   | db_fitchain_equipo            |
| Establecimiento  | 8084   | db_fitchain_establecimiento   |
| Asistencia       | 8085   | db_fitchain_asistencia        |
| Horario          | 8086   | db_fitchain_horario           |
| Reserva          | 8087   | db_fitchain_reserva           |
| Membresía        | 8088   | db_fitchain_membresia         |
| Pago             | 8089   | db_fitchain_pago              |
| Notificación     | 8090   | db_fitchain_notificacion      |
| Gateway          | 8091   | (sin base de datos)           |
| Auth             | 8092   | db_fitchain_auth              |

---

## Funcionalidades implementadas

- Gestión completa de clientes con asignación a entrenador y establecimiento
- Gestión de entrenadores con especialidad y establecimiento asignado
- Control de equipos por establecimiento con resumen por tipo
- Administración de establecimientos con vista de entrenadores, clientes y equipos
- Registro de asistencias vinculadas a cliente y horario
- Gestión de horarios por establecimiento y día de la semana
- Reservas de actividades con estado (PENDIENTE, CONFIRMADA, CANCELADA)
- Membresías con tipos MENSUAL, TRIMESTRAL y ANUAL con cálculo automático de fecha fin
- Registro de pagos por cliente con distintos métodos de pago
- Envío de notificaciones a clientes por tipo y estado
- Autenticación con JWT, encriptación BCrypt y roles: ADMIN, ENTRENADOR, CLIENTE
- API Gateway con filtro JWT para todas las rutas protegidas
- Comunicación entre microservicios mediante WebClient

---

## Tecnologías

- Java 21
- Spring Boot 3.2.5
- Spring Cloud Gateway
- Spring Security + JWT (jjwt 0.11.5)
- Spring Data JPA + Hibernate
- MySQL (XAMPP)
- Lombok
- Bean Validation (JSR 380)
- WebClient (WebFlux)

---

## Requisitos previos

- Java 21
- Maven 3.9+
- XAMPP con MySQL activo
- IntelliJ IDEA (recomendado)

---

## Pasos para ejecutar

### 1. Crear las bases de datos en MySQL

Ejecutar en phpMyAdmin o consola MySQL:

```sql
CREATE DATABASE db_fitchain_clientes;
CREATE DATABASE db_fitchain_entrenadores;
CREATE DATABASE db_fitchain_equipo;
CREATE DATABASE db_fitchain_establecimiento;
CREATE DATABASE db_fitchain_asistencia;
CREATE DATABASE db_fitchain_horario;
CREATE DATABASE db_fitchain_reserva;
CREATE DATABASE db_fitchain_membresia;
CREATE DATABASE db_fitchain_pago;
CREATE DATABASE db_fitchain_notificacion;
CREATE DATABASE db_fitchain_auth;
```

### 2. Iniciar los microservicios

Abrir cada proyecto en IntelliJ y ejecutar en este orden:

```
1. Auth          (puerto 8092)
2. Cliente       (puerto 8081)
3. Entrenador    (puerto 8082)
4. Establecimiento (puerto 8084)
5. Equipo        (puerto 8083)
6. Horario       (puerto 8086)
7. Asistencia    (puerto 8085)
8. Reserva       (puerto 8087)
9. Membresía     (puerto 8088)
10. Pago         (puerto 8089)
11. Notificación (puerto 8090)
12. Gateway      (puerto 8091)
```

O por terminal en la carpeta de cada microservicio:

```bash
./mvnw spring-boot:run
```

### 3. Obtener token de acceso

```
POST http://localhost:8091/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Usuarios disponibles por defecto:

| Username     | Password        | Rol        |
|--------------|-----------------|------------|
| admin        | admin123        | ADMIN      |
| entrenador1  | entrenador123   | ENTRENADOR |
| cliente1     | cliente123      | CLIENTE    |

### 4. Usar el token en las peticiones

Agregar en el header de cada request:

```
Authorization: Bearer <token_obtenido>
```

---
## Arquitectura

```
Cliente/Postman
      │
      ▼
API Gateway (8091)  ←── Valida JWT con Auth Service
      │
      ├── /v1/clientes/**        → Cliente (8081)
      ├── /v1/entrenadores/**   → Entrenador (8082)
      ├── /v1/equipos/**         → Equipo (8083)
      ├── /v1/establecimientos/**→ Establecimiento (8084)
      ├── /v1/asistencias/**     → Asistencia (8085)
      ├── /v1/horarios/**        → Horario (8086)
      ├── /v1/reservas/**        → Reserva (8087)
      ├── /v1/membresias/**      → Membresía (8088)
      ├── /v1/pagos/**           → Pago (8089)
      ├── /v1/notificaciones/**  → Notificación (8090)
      └── /v1/auth/**            → Auth (8092)
```
