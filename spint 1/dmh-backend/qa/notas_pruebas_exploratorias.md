# Notas de Pruebas Exploratorias - Sprint 1 - Digital Money House

## Objetivo
Detectar errores no anticipados, inconsistencias o problemas de usabilidad en los flujos de autenticación de la billetera virtual (registro, login y logout) usando pruebas no estructuradas basadas en experiencia e intuición del tester.

## Organización de la exploración
Se realizaron sesiones estructuradas por tours:

### Tour 1: Registro de usuarios
- **Escenarios ejecutados**:
  - Registro con datos válidos.
  - Registro con email ya registrado.
  - Registro con formato inválido de email.
  - Registro con campos vacíos.
- **Hallazgos**:
  - DEF-002: El campo email permite formato inválido, acepta "usuario@" como correcto.

### Tour 2: Inicio de sesión
- **Escenarios ejecutados**:
  - Login con credenciales válidas.
  - Login con email inexistente.
  - Login con contraseña incorrecta.
  - Campos vacíos.
- **Hallazgos**:
  - DEF-003: El login falla incluso con credenciales correctas.
  - Mensajes de error no son claros para el usuario.

### Tour 3: Logout
- **Escenarios ejecutados**:
  - Logout tras login exitoso.
  - Logout sin token.
  - Logout con token inválido.
- **Hallazgos**:
  - DEF-004: Al hacer logout, el token no se invalida correctamente. El usuario puede seguir navegando.

## Conclusiones
Las pruebas exploratorias permitieron descubrir errores críticos en validaciones y lógica de sesión. Se recomienda priorizar la corrección de DEF-002, DEF-003 y DEF-004 antes de avanzar al siguiente sprint.

## Recomendaciones
- Mejorar validaciones de formulario desde backend.
- Asegurar invalidación del token al cerrar sesión.
- Incluir mensajes de error informativos y diferenciados.

---
**Versión del documento**: 1.0  
**Fecha de emisión**: 2025-08-08  
**Autor**: QA Tester Digital Money House