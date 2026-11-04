# Backend Clínica PetSalud – Seguridad (Spring Security + JWT)

Backend Spring Boot 3.3 / Java 17 / MySQL con autenticación JWT stateless y autorización por roles.

## Roles
| Rol | Alcance |
|---|---|
| CLIENTE | Sus propios datos, mascotas, citas, expedientes, notificaciones y recompensas |
| EMPLEADO | Operación diaria: citas, expedientes, inventario, notificaciones, consulta de clientes |
| GERENTE | Todo lo anterior + gestión de usuarios, eliminaciones y ventas globales |

## Ejecución
1. MySQL 8 en `localhost:3307` (la base `petsalud_db` se crea sola).
2. Variables de entorno: `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (Base64, ≥256 bits; hay un valor solo-desarrollo por defecto).
3. `./mvnw spring-boot:run` (puerto 8080).

Usuarios demo (solo desarrollo, `app.seed-demo-users`): `gerente@petsalud.cl / Gerente123!`, `empleado@petsalud.cl / Empleado123!`, `cliente@petsalud.cl / Cliente123!`.

## Uso
- `POST /api/auth/registro` → crea CLIENTE (el rol nunca viene del body).
- `POST /api/auth/login` → devuelve `{ token, tipo: "Bearer", ... }`.
- Resto de la API: header `Authorization: Bearer <token>`.

## Medidas de seguridad
- BCrypt para contraseñas; JWT HS256 con secreto externo y expiración (1 h por defecto).
- `JwtAuthenticationFilter` valida el token en cada request; sesión STATELESS.
- `@PreAuthorize` en todos los endpoints + reglas de propiedad (`@seguridad`) contra IDOR.
- Bloqueo temporal tras 5 logins fallidos y registro de auditoría (`SEGURIDAD_AUDITORIA`).
- Respuestas 401/403 en JSON; mensajes genéricos en login (sin enumeración de usuarios).
- Validación de entrada (Bean Validation), escape HTML contra XSS almacenado, JPA con consultas parametrizadas (SQL Injection), cabeceras CSP / X-Frame-Options / nosniff.
- CSRF deshabilitado a propósito: API stateless con token en header, sin cookies de sesión.

## Pruebas
Importar `docs/PetSalud-seguridad.postman_collection.json` en Postman y ejecutar la colección en orden.
