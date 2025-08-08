# Plan de Pruebas - Sprint 1 - Digital Money House

## 1. Objetivo
Diseñar y ejecutar un conjunto de pruebas que aseguren la calidad de los endpoints de autenticación (registro, login y logout) desarrollados en el Sprint 1 de la billetera virtual Digital Money House.

## 2. Alcance
Este plan cubre:
- Registro de usuarios (POST /api/users)
- Inicio de sesión (POST /api/login)
- Cierre de sesión (POST /api/logout)

## 3. Tipos de prueba
- Pruebas manuales funcionales
- Smoke testing
- Regresión
- Pruebas exploratorias

## 4. Criterios de inclusión
### 4.1. Smoke test
Un caso se incluye en esta suite si:
- Valida una funcionalidad crítica del negocio (registro/login/logout).
- Puede ejecutarse rápidamente con datos controlados.

### 4.2. Regresión
Un caso se incluye en esta suite si:
- Verifica funcionalidades previamente desarrolladas.
- Puede detectar efectos colaterales de nuevas implementaciones.

## 5. Estructura de un caso de prueba
- **ID**: Código identificador (ej: TC-REG-001)
- **Título**: Breve descripción de lo que valida
- **Endpoint**: Método y ruta probada
- **Precondiciones**: Estado inicial necesario
- **Datos de entrada**: Body o headers requeridos
- **Pasos**: Secuencia de acciones
- **Resultado esperado**: Respuesta y código HTTP
- **Resultado obtenido**: Al ejecutar
- **Clasificación**: Smoke o regresión

## 6. Cómo reportar un defecto
- **ID**: DEF-### correlativo
- **Título**: Descripción corta del fallo
- **Descripción**: Detalles del comportamiento inesperado
- **Pasos para reproducir**: Acciones exactas
- **Resultado esperado vs actual**
- **Severidad**: Crítica / Alta / Media / Baja
- **Prioridad**: Alta / Media / Baja

## 7. Herramientas
- Cliente Postman o RestAssured (manual o automático)
- Hojas de cálculo Excel/Google Sheets para seguimiento manual
- GitLab para almacenamiento y control de versiones

## 8. Matriz de pruebas
Los casos de prueba están organizados en una hoja de cálculo, junto con los resultados obtenidos, clasificados como "Smoke" o "Regresión" según corresponda.

---
**Versión del documento**: 1.0  
**Fecha de emisión**: 2025-08-08  
**Autor**: QA Tester Digital Money House