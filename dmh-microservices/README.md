\# Digital Money House — Microservicios



Proyecto académico migrado de monolito a microservicios con \*\*Spring Boot 3\*\*, \*\*Spring Cloud\*\*, \*\*JWT\*\*, \*\*Eureka Server\*\*, \*\*Spring Cloud Gateway\*\* y \*\*MySQL\*\*.



\## 🏗️ Arquitectura



\- \*\*eureka-server\*\*: Service Registry (puerto 8761).

\- \*\*api-gateway\*\*: Entrada única a los servicios (puerto 8080).

\- \*\*user-service\*\*: Registro, login y perfil de usuario.

\- \*\*account-service\*\*: Gestión de cuentas (saldo, alias, CVU).

\- \*\*card-service\*\*: CRUD de tarjetas.

\- \*\*transaction-service\*\*: Movimientos, ingresos y transferencias.

\- \*\*mysql\*\*: Base de datos relacional (puerto 3306).



Cada servicio es un proyecto independiente con:

\- `spring.application.name` configurado.

\- Registro en Eureka.

\- Seguridad con JWT HS256.

\- Manejo global de excepciones con `@ControllerAdvice`.

\- Documentación con Swagger (`/swagger-ui`).



---



\## 🚀 Requisitos



\- Docker y Docker Compose.

\- JDK 17 + Maven (si deseas correr local sin contenedores).



---



\## ▶️ Levantar el stack con Docker Compose



En la raíz del proyecto:



```bash

docker compose up --build



