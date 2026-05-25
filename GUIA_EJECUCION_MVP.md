# Guia para correr el MVP de RestoSync

Este documento explica que hacer despues de descargar o clonar el proyecto para levantar el MVP completo: backend, base de datos y frontend.

## Estructura del proyecto

```text
MVP-RestoSync/
+-- Backend/
|   +-- comandas/        # API REST + WebSocket con Spring Boot
+-- ViewRestoSync/       # Frontend React + Vite
```

## Tecnologias y versiones usadas

### Backend

| Tecnologia | Version |
| --- | --- |
| Java | 21 requerido por `pom.xml` |
| Java local probado | 22.0.2 |
| Spring Boot | 4.0.6 |
| Maven Wrapper | 3.3.4 |
| Maven distribuido por wrapper | 3.9.15 |
| MySQL | 8.x recomendado |
| Spring Web | Gestionado por Spring Boot |
| Spring Security | Gestionado por Spring Boot |
| Spring Data JPA | Gestionado por Spring Boot |
| Spring WebSocket/STOMP | Gestionado por Spring Boot |
| MySQL Connector/J | Gestionado por Spring Boot |
| JJWT | 0.11.5 |
| Lombok | 1.18.36 |
| MapStruct | 1.6.3 |
| Springdoc OpenAPI | 2.8.8 |

### Frontend

| Tecnologia | Version |
| --- | --- |
| Node.js local probado | 24.11.1 |
| npm local probado | 11.6.2 |
| React | 19.2.6 |
| React DOM | 19.2.6 |
| Vite | 8.0.12 |
| TypeScript | 6.0.2 |
| React Router DOM | 7.15.1 |
| TanStack React Query | 5.100.14 |
| Zustand | 5.0.13 |
| Axios | 1.16.1 |
| STOMP JS | 7.3.0 |
| SockJS Client | 1.6.1 |
| Lucide React | 1.16.0 |
| ESLint | 10.3.0 |

## Puertos usados

| Servicio | URL |
| --- | --- |
| Backend API | `http://localhost:8080/api` |
| Backend WebSocket | `http://localhost:8080/ws` |
| Swagger/OpenAPI | `http://localhost:8080/swagger-ui.html` |
| Frontend dev | `http://localhost:5173` |
| Frontend preview | `http://localhost:4173` |

## Requisitos previos

Antes de iniciar, instala:

1. Java JDK 21 o superior.
2. MySQL Server 8.x.
3. Node.js y npm.
4. Git, si se va a clonar desde repositorio.
5. Maven instalado globalmente solo si el wrapper `mvnw` no funciona en tu entorno.

Verifica versiones:

```bash
java -version
node -v
npm -v
```

Si tienes Maven global:

```bash
mvn -v
```

## 1. Preparar la base de datos

Abre MySQL y crea la base de datos esperada por el backend:

```sql
CREATE DATABASE restosync_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

Luego revisa este archivo:

```text
Backend/comandas/src/main/resources/application.properties
```

Configura tus credenciales locales de MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/restosync_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD_LOCAL
```

Nota: el proyecto actualmente trae una clave local en `application.properties`. Para correrlo en otra maquina, cambia ese valor por la clave real de tu MySQL.

## 2. Levantar el backend

Desde la raiz del proyecto:

```bash
cd Backend/comandas
```

En Windows:

```bash
.\mvnw.cmd spring-boot:run
```

En macOS/Linux:

```bash
./mvnw spring-boot:run
```

Si el Maven Wrapper falla, instala Maven 3.9.x y usa:

```bash
mvn spring-boot:run
```

Cuando el backend levante correctamente, debe quedar escuchando en:

```text
http://localhost:8080
```

Puedes revisar Swagger en:

```text
http://localhost:8080/swagger-ui.html
```

## 3. Inicializacion de datos

El backend ejecuta estos scripts al arrancar:

```text
Backend/comandas/src/main/resources/schema.sql
Backend/comandas/src/main/resources/data.sql
```

Esto sucede porque en `application.properties` esta configurado:

```properties
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
spring.sql.init.data-locations=classpath:data.sql
spring.sql.init.continue-on-error=true
spring.jpa.hibernate.ddl-auto=update
```

Que significa:

- `schema.sql` crea las tablas si no existen.
- `data.sql` inserta datos iniciales con `INSERT IGNORE`, por lo que usuarios/productos base no deberian duplicarse.
- Algunas sentencias `UPDATE` refrescan datos de ejemplo.
- La base no se elimina automaticamente al apagar Spring.

Si quieres reiniciar la demo desde cero:

```sql
DROP DATABASE restosync_db;
CREATE DATABASE restosync_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

Despues vuelve a levantar el backend.

## 4. Levantar el frontend

Abre otra terminal desde la raiz del proyecto:

```bash
cd ViewRestoSync
npm install
npm run dev
```

La app deberia abrir en:

```text
http://localhost:5173
```

Si Vite usa otro puerto porque `5173` esta ocupado, usa la URL que muestre la terminal.

## 5. Variables opcionales del frontend

El frontend ya trae valores por defecto:

```text
API: http://localhost:8080/api
WS:  http://localhost:8080/ws
```

Si necesitas cambiarlos, crea este archivo:

```text
ViewRestoSync/.env.local
```

Con:

```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_WS_URL=http://localhost:8080/ws
```

Luego reinicia `npm run dev`.

## 6. Usuarios de prueba

Todos los usuarios iniciales usan la misma contrasena:

```text
123456
```

| Rol | Email | Password |
| --- | --- | --- |
| Administrador | `admin@restosync.com` | `123456` |
| Mesero | `james@restosync.com` | `123456` |
| Mesero | `elena@restosync.com` | `123456` |
| Cocinero | `mike@restosync.com` | `123456` |
| Bartender | `roberto@restosync.com` | `123456` |

## 7. Comandos utiles

### Frontend

```bash
cd ViewRestoSync
npm run dev
npm run lint
npm run build
npm run preview
```

### Backend

Con wrapper:

```bash
cd Backend/comandas
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test
.\mvnw.cmd clean package -DskipTests
```

Con Maven global:

```bash
cd Backend/comandas
mvn spring-boot:run
mvn test
mvn clean package -DskipTests
```

## 8. Orden recomendado para correr el MVP

1. Iniciar MySQL.
2. Crear `restosync_db` si no existe.
3. Ajustar credenciales en `application.properties`.
4. Levantar backend desde `Backend/comandas`.
5. Levantar frontend desde `ViewRestoSync`.
6. Entrar a `http://localhost:5173/login`.
7. Probar con cualquiera de los usuarios iniciales.

## 9. Problemas comunes

### El frontend muestra errores 401 o redirige al login

Puede haber un token vencido o invalido en `localStorage`. Cierra sesion o borra el almacenamiento del navegador para `localhost:5173`.

### El backend no conecta a MySQL

Revisa:

- Que MySQL este iniciado.
- Que exista la base `restosync_db`.
- Que `spring.datasource.username` y `spring.datasource.password` sean correctos.
- Que el puerto de MySQL sea `3306`.

### El frontend no conecta al backend

Revisa:

- Que el backend este en `http://localhost:8080`.
- Que `VITE_API_BASE_URL` apunte a `http://localhost:8080/api`.
- Que no haya otro proceso usando el puerto `8080`.

### WebSocket no actualiza cocina/bar/mesero

Revisa:

- Que `VITE_WS_URL` apunte a `http://localhost:8080/ws`.
- Que el backend este activo.
- Que el frontend este corriendo en un origen permitido: `http://localhost:5173` o `http://localhost:4173`.

## 10. Nota de seguridad

Este MVP incluye credenciales, datos demo y un `jwt.secret` de desarrollo. Antes de pensar en produccion hay que mover secretos a variables de entorno, endurecer configuracion, revisar CORS, revisar logs y preparar perfiles separados para desarrollo/produccion.
