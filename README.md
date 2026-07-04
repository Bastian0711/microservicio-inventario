# Microservicio Inventario

## Descripción

Microservicio encargado de gestionar el inventario y stock de productos dentro del sistema de eventos gastronómicos.

## Funcionalidades

* Crear registros de inventario
* Listar inventario
* Buscar inventario por ID
* Actualizar inventario
* Eliminar inventario
* Consultar stock disponible
* Descontar stock
* Comunicación con microservicio de productos

## Tecnologías utilizadas

* Java 21
* Spring Boot
* MySQL
* Maven
* Docker
* Docker Compose
* OpenFeign

## Ejecución del proyecto

```bash
docker compose up -d
```

## Endpoints principales

### Obtener inventario

GET /api/v2/inventario

### Obtener inventario por ID

GET /api/v2/inventario/{id}

### Crear inventario

POST /api/v2/inventario

### Actualizar inventario

PUT /api/v2/inventario/{id}

### Eliminar inventario

DELETE /api/v2/inventario/{id}

### Consultar stock

GET /api/v2/inventario/stock/{productoId}

### Descontar stock

PUT /api/v2/inventario/descontar

## Comunicación entre microservicios

Este microservicio se comunica con el microservicio de productos utilizando Feign Client para validar la existencia de productos antes de realizar operaciones de inventario.

### Endpoint consumido

GET /api/v2/productos/{id}

## Configuración de comunicación

Variable de entorno utilizada:

```properties
PRODUCTO_SERVICE_URL
```

Ejemplo de URL:

```text
http://18.210.19.5:8084/api/v2/productos/1
```

## Validaciones

* Validación de campos obligatorios
* Validación de stock positivo
* Manejo de errores controlados con Bean Validation
* Manejo de errores cuando el microservicio de productos no está disponible
