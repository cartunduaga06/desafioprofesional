# Digital Money House - Sprint 1

Este proyecto es una API REST construida con Java + Spring Boot como parte del desafío profesional de backend de Digital House. Implementa funcionalidades de registro, login y logout de usuarios, con seguridad JWT y base de datos MySQL.

## 🚀 Tecnologías usadas

- Java 17
- Spring Boot
- JWT
- MySQL
- Docker
- Swagger

## ⚙️ Instalación local

1. Clonar el repositorio:
```bash
git clone https://github.com/tuusuario/dmh-backend.git
cd dmh-backend
```

2. Levantar la base de datos y backend con Docker:
```bash
docker-compose up
```

3. Acceder a la API:
```
http://localhost:8080/swagger-ui.html
```

## 🔐 Endpoints principales

- `POST /auth/register`: Registro de usuario (autogenera CVU y alias)
- `POST /auth/login`: Login con email y password, retorna JWT
- `POST /auth/logout`: Logout y revocación de token

## 🧪 Testing

Las pruebas manuales se encuentran en `/qa/casos-de-prueba.xlsx`. Se realizaron pruebas smoke y regresión. También se ejecutaron pruebas exploratorias documentadas.

## 📂 Estructura

```
/backend - Código fuente Spring Boot
/docker  - Configuración Docker
/qa      - Pruebas manuales y documentos
alias.txt - Lista de palabras para generar alias
```

## 📌 Autor

Carlos (Digital House - Desafío profesional Backend)
