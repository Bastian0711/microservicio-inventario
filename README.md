# Microservicio Inventario

## Descripción

Microservicio encargado de gestionar el inventario de productos dentro de **ReadyStand**, una plataforma web para eventos gastronómicos (ferias universitarias, festivales u otras actividades organizadas por instituciones). Cada stand del evento ofrece productos con un stock asociado, y este microservicio es responsable de llevar esa existencia: cuánto stock tiene cada producto, descontarlo automáticamente cuando se confirma una compra, y evitar que se venda algo sin disponibilidad real.

Dentro de la arquitectura general, Inventario depende del microservicio de **Producto** (valida que el producto exista y esté disponible antes de registrar o descontar stock) y es consumido, a su vez, por el microservicio de **Pedido**, que verifica disponibilidad (`GET /api/inventarios/producto/{id}`) y descuenta stock (`POST /api/inventarios/descontar`) al confirmar una compra.

Requerimientos funcionales que cubre este microservicio, según el documento de arquitectura:

| Req. | Descripción |
|---|---|
| R.33 — Registrar stock de productos | Permite definir la cantidad inicial de stock disponible por producto. |
| R.34 — Descontar stock automáticamente | Reduce el stock cuando el pedido asociado es confirmado, reflejando la cantidad restante. |
| R.35 — Validar disponibilidad de stock | Verifica que exista cantidad suficiente antes de permitir la compra; bloquea la operación si no la hay. |
| R.36 — Bloquear producto sin stock | (Responsabilidad compartida con Producto) Cuando el stock llega a cero, el producto debe quedar marcado como no disponible. |

> Nota sobre R.36: en el código actual, este microservicio descuenta el stock y devuelve el stock restante, pero no encontré lógica que cambie automáticamente el estado `disponible` del producto en el microservicio de Producto cuando el stock llega a cero — eso parecería pendiente de implementar o vivir del lado de Producto.

## Funcionalidades

* Registrar inventario para un producto
* Listar todo el inventario
* Buscar inventario por ID
* Buscar inventario por ID de producto
* Actualizar el stock de un inventario existente
* Descontar stock de un producto (con validación de stock suficiente)
* Eliminar un registro de inventario
* Comunicación con el microservicio de **Productos** (vía Feign) para validar existencia y disponibilidad
* Registro en Eureka como cliente de descubrimiento de servicios
* Documentación interactiva con Swagger / OpenAPI

## Tecnologías utilizadas

* Java 21
* Spring Boot 3.5.14
* Spring Data JPA
* Spring Cloud OpenFeign
* Spring Cloud Netflix Eureka Client
* MySQL 8.0
* springdoc-openapi (Swagger UI)
* Lombok
* Maven
* Docker / Docker Compose
* H2 (para pruebas)

## Arquitectura y flujo principal

> Dentro del flujo de compra de ReadyStand, este microservicio no es llamado directamente por el cliente final: es el microservicio de **Pedido** quien lo consulta (verificar disponibilidad) y lo descuenta (al confirmar el pedido). Los endpoints de creación/actualización/eliminación descritos aquí son de gestión (por ejemplo, para que un stand registre stock inicial).

### Registrar inventario

1. El cliente envía `idProducto`, `nombreProducto`, `categoria`, `stock` y `precio`.
2. El servicio consulta el producto en el microservicio de Productos (`producto.service.url`).
3. Si el producto no existe, responde `404 NOT_FOUND`. Si el servicio de Productos no está disponible, responde `503 SERVICE_UNAVAILABLE`.
4. Si el producto existe pero no está disponible (`disponible = false`), se rechaza con `409 CONFLICT`.
5. Si el producto es válido, se crea el registro de inventario.

### Descontar stock

1. El cliente envía `idProducto` y `cantidad`.
2. Se valida nuevamente que el producto exista y esté disponible en el microservicio de Productos.
3. Se busca el inventario asociado a ese producto; si no existe, `404 NOT_FOUND`.
4. Si la cantidad solicitada supera el stock actual, se rechaza con `409 CONFLICT` (stock insuficiente).
5. Si todo es válido, se descuenta el stock y se retorna el stock restante.

## Ejecución del proyecto

> El código fuente vive en `codigo-fuente/backend-inventario/`. Hay dos formas de levantarlo:

### Opción 1: entorno de desarrollo (contenedor Maven + MySQL)

```bash
docker compose up -d
```

Esto levanta:
* `mysql_servidor_inventario`: base de datos MySQL en el puerto `3308` (host) → `3306` (contenedor).
* `entorno_inventario`: contenedor con Maven y JDK 21 montando el código fuente (`./codigo-fuente`) en `/app`, listo para compilar y ejecutar la aplicación manualmente (por ejemplo, con `mvn spring-boot:run` dentro del contenedor). Recibe la variable de entorno `PRODUCTO_SERVICE_URL` con la URL del microservicio de Productos.

### Opción 2: imagen de la aplicación (usando el `Dockerfile`)

> Nota: el `docker-compose.yml` no construye ni usa este `Dockerfile`; es una alternativa manual para empaquetar la app en una imagen propia. Ejecutar estos comandos dentro de `codigo-fuente/backend-inventario/`.

```bash
mvn clean package -DskipTests
docker build -t microservicio-inventario .
docker run -p 8090:8090 microservicio-inventario
```

> El servicio escucha en el puerto **8090** (`server.port=8090` en `application.properties`). El `Dockerfile` declara `EXPOSE 8080`, pero esa línea es solo informativa: no coincide con el puerto real de la app, no cambia dónde escucha Spring Boot ni afecta el mapeo con `-p`. Se recomienda corregir ese `EXPOSE` a `8090` para evitar confusiones.

### Configuración relevante

En `application.properties`:

* `spring.datasource.url`: conexión a MySQL (`inventario_backend`).
* `producto.service.url`: URL del microservicio de Productos (consumido internamente en `/api/v3/productos/{id}`); puede sobreescribirse con la variable de entorno `PRODUCTO_SERVICE_URL`.
* `eureka.client.service-url.defaultZone`: URL del servidor Eureka.
* `springdoc.swagger-ui.path=/doc/swagger-ui.html`: ruta de la documentación Swagger.

## Endpoints principales

Base path: `/api/v3/inventario`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/v3/inventario` | Lista todo el inventario |
| GET | `/api/v3/inventario/{id}` | Busca un registro de inventario por ID |
| GET | `/api/v3/inventario/producto/{idProducto}` | Busca el inventario asociado a un producto |
| POST | `/api/v3/inventario` | Registra un nuevo inventario |
| POST | `/api/v3/inventario/descontar` | Descuenta stock de un producto |
| PUT | `/api/v3/inventario/{id}` | Actualiza el stock de un inventario existente |
| DELETE | `/api/v3/inventario/{id}` | Elimina un registro de inventario |

### Registrar inventario

`POST /api/v3/inventario`

```json
{
  "idProducto": 1,
  "nombreProducto": "Sushi",
  "categoria": "Platos preparados",
  "stock": 50,
  "precio": 6990.0
}
```

### Actualizar inventario

`PUT /api/v3/inventario/{id}`

```json
{
  "stock": 45
}
```

### Descontar stock

`POST /api/v3/inventario/descontar`

```json
{
  "idProducto": 1,
  "cantidad": 5
}
```

Respuesta:

```json
{
  "stockRestante": 45
}
```

## Modelo de datos (respuesta `InventarioDTO`)

```json
{
  "id": 1,
  "idProducto": 1,
  "nombreProducto": "Sushi",
  "categoria": "Platos preparados",
  "stock": 45,
  "precio": 6990.0
}
```

## Validaciones y manejo de errores

* Campos obligatorios en la creación (`idProducto`, `nombreProducto`, `categoria`, `stock`, `precio`) mediante Bean Validation.
* `idProducto` debe ser positivo; `precio` debe ser positivo; `stock` no puede ser negativo.
* Solo se puede registrar o descontar inventario de productos que existan y estén **disponibles** en el microservicio de Productos.
* No se puede descontar una cantidad de stock mayor a la disponible.
* No se puede operar sobre un registro de inventario inexistente.

Los errores se devuelven en un formato consistente mediante un manejador global de excepciones:

```json
{
  "error": "NOT_FOUND",
  "message": "Inventario no encontrado"
}
```

| Código de error | HTTP Status | Causa |
|---|---|---|
| `VALIDATION_ERROR` | 400 | Falla de validación de campos (Bean Validation) |
| `NOT_FOUND` | 404 | Inventario o producto no encontrado |
| `BUSINESS_ERROR` | 409 | Regla de negocio incumplida (producto no disponible, stock insuficiente) |
| `SERVICE_UNAVAILABLE` | 503 | Microservicio de Productos no disponible |

## Documentación de la API

Con la aplicación en ejecución, la documentación Swagger UI está disponible en:

```
http://localhost:8090/doc/swagger-ui.html
```

## Pruebas

El proyecto incluye pruebas unitarias e de integración para el modelo, repositorio, servicio y controlador (`codigo-fuente/backend-inventario/src/test/java`), ejecutables con:

```bash
mvn test
```

Las reglas de negocio críticas cubiertas (detalladas en `TESTING.PLAN.md`) incluyen:

* Solo se registra inventario de productos existentes y disponibles.
* Si el servicio de Productos no está disponible, se lanza `ServicioNoDisponibleException`.
* No se puede descontar stock por encima del disponible.
* No se puede operar sobre inventario inexistente.
* Al descontar stock, se retorna el stock restante actualizado.
