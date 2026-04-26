# API Security App

API REST en Spring Boot para autenticacion con JWT y analisis de fraude (texto o imagen) usando OpenAI.

## Stack

- Java 21
- Spring Boot 3.3
- Spring Security + JWT
- PostgreSQL
- Maven

## Variables de entorno

Configura estas variables antes de ejecutar:

- `SUPABASE_DB_URL` ejemplo: `jdbc:postgresql://<pooler-host>:6543/postgres?sslmode=require&prepareThreshold=0`
- `SUPABASE_DB_USER` ejemplo: `postgres.<project-ref>`
- `SUPABASE_DB_PASSWORD`
- `OPENAI_API_KEY`
- `JWT_SECRET_BASE64` (clave base64 de al menos 256 bits)

## Ejecutar

```bash
mvn spring-boot:run
```

La API corre por defecto en `http://localhost:8080`.

## Endpoints

### Registro

- `POST /api/v1/auth/register`

Body:

```json
{
  "username": "test1",
  "password": "Test1234!"
}
```

### Login

- `POST /api/v1/auth/login`

Body:

```json
{
  "username": "test1",
  "password": "Test1234!"
}
```

Respuesta: token JWT.

### Analisis de fraude

- `POST /api/v1/analyze`
- Header: `Authorization: Bearer <token>`

Body ejemplo (texto):

```json
{
  "text": "Tu cuenta sera bloqueada, valida aqui: http://bit.ly/fake",
  "channel": "SMS"
}
```

Reglas del request:

- enviar `text` o `imageBase64`
- no enviar ambos al mismo tiempo

## Probar con Thunder Client

1. Crear request de register.
2. Crear request de login y copiar `token`.
3. Crear request de analyze con header `Bearer`.
4. Verificar `401` sin token y `200` con token valido.
