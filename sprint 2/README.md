# Digital Money House – Backend (Sprint 1)

Este repositorio contiene la implementación del backend para el proyecto **Digital Money House**, desarrollado con **Java 17** y **Spring Boot 3.x**. El objetivo del Sprint 1 es ofrecer un producto mínimo viable (MVP) que permita registrar usuarios, autenticar y cerrar sesión mediante JWT, cumpliendo con los criterios de aceptación definidos.

## Endpoints implementados

| Método | Ruta                                   | Descripción |
|-------|----------------------------------------|-------------|
| **POST** | `/users/register` | Registra un nuevo usuario (nombre, apellido, DNI, email, teléfono y contraseña). Genera un CVU de 22 dígitos, alias de tres palabras y saldo inicial 0. |
| **POST** | `/auth/login` | Autentica al usuario y devuelve un JWT firmado con HS256. |
| **POST** | `/user/logout` | Invalida el JWT actual (cierre de sesión lógico). |
| **GET** | `/users/{id}` | Devuelve el perfil del usuario (sin contraseña). |
| **PATCH** | `/users/{id}` | Actualiza parcialmente los datos del usuario (nombre, apellido, DNI, teléfono, email). |
| **GET** | `/accounts/{id}` | Devuelve datos de la cuenta (cvu, alias, saldo). |
| **PATCH** | `/accounts/{id}` | Actualiza datos de la cuenta (alias). |
| **GET** | `/accounts/{id}/transactions` | Devuelve las últimas cinco transacciones del usuario. |
| **GET** | `/accounts/{id}/cards` | Lista todas las tarjetas asociadas al usuario. |
| **GET** | `/accounts/{id}/cards/{cardId}` | Devuelve la tarjeta específica de la cuenta. |
| **POST** | `/accounts/{id}/cards` | Asocia una nueva tarjeta a la cuenta. |
| **DELETE** | `/accounts/{id}/cards/{cardId}` | Elimina una tarjeta de la cuenta. |

La documentación completa de las API está disponible en `/swagger-ui.html` gracias a **Spring doc**.

## Configuración

El proyecto utiliza variables de entorno para configurar la base de datos y parámetros de JWT. Puedes establecerlas manualmente o dejar que tomen los valores predeterminados definidos en `application.properties`.

- `SPRING_DATASOURCE_URL`: URL de conexión a MySQL. Valor por defecto: `jdbc:mysql://localhost:3306/digitalmoneyhouse?useSSL=false&serverTimezone=UTC`.
- `SPRING_DATASOURCE_USERNAME`: usuario de la base de datos. Por defecto `root`.
- `SPRING_DATASOURCE_PASSWORD`: contraseña de la base de datos. Por defecto `your_password`.
- `JWT_SECRET`: clave secreta base64 para firmar tokens. Por defecto `UmFuZG9tU2VjcmV0S2V5Rm9yRGlnaXRhbE1vbmV5SG91c2U=` (equivalente a `RandomSecretKeyForDigitalMoneyHouse`).
- `JWT_EXPIRATION_MS`: tiempo de expiración del token en milisegundos (por defecto un día – `86400000`).

## Cómo compilar y ejecutar localmente

Requisitos previos:

1. **Java 17** y **Maven 3.9** o superior.
2. **MySQL 8.x** en ejecución, con una base de datos llamada `digitalmoneyhouse` y credenciales de acceso.

Pasos:

```bash
cd dmh-backend
mvn clean package -DskipTests
java -jar target/dmh-app.jar
```

La aplicación quedará disponible en `http://localhost:8080`.

## Ejecución con Docker

El proyecto incluye un `Dockerfile` y un `docker-compose.yml` para facilitar la construcción y despliegue. Para levantar la aplicación junto con una base de datos MySQL ejecuta:

```bash
docker-compose up --build
```

Esto iniciará dos contenedores:

- **dmh-mysql**: instancia de MySQL con la base de datos `digitalmoneyhouse`.
- **dmh-backend**: aplicación Spring Boot. Expone el puerto `8080` en tu máquina local.

Puedes acceder a la documentación Swagger en `http://localhost:8080/swagger-ui.html`.

## Notas de diseño

- **JWT**: se utiliza el algoritmo HS256 con una clave secreta configurable. Los tokens tienen expiración configurable vía `JWT_EXPIRATION_MS`.
- **Logout**: el cierre de sesión se implementa invalidando el token actual en memoria. En un entorno real sería recomendable persistir los tokens revocados en un almacén externo (por ejemplo Redis) para soportar despliegues horizontales.
- **Alias y CVU**: el alias se genera a partir de tres palabras pseudoaleatorias extraídas de `alias.txt`. El CVU es un número único de 22 dígitos. El servicio garantiza que ambos valores sean únicos en la base de datos.

## Pruebas

Pendiente de implementación en Sprints posteriores. La arquitectura se ha diseñado para facilitar la incorporación de pruebas unitarias (JUnit) e integradas (RestAssured). También se recomienda elaborar un plan de pruebas manuales, incluyendo casos de smoke testing y regresión.

## Licencia

Este proyecto se proporciona con fines académicos como parte del desafío **Certificación Backend**.