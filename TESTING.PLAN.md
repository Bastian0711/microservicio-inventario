# TESTING_PLAN.md — Microservicio Inventario

## Pruebas Unitarias y Cobertura de Reglas de Negocio

Este documento resume las reglas de negocio críticas del microservicio de Inventario y el estado actual de cobertura mediante pruebas unitarias.

El microservicio fue probado en cuatro capas principales: modelo, servicio, repositorio y controlador.

---

## Reglas de Negocio Críticas

1. Solo se puede registrar inventario para productos que existan y estén disponibles en el microservicio de Productos.
2. Si el servicio de Productos no está disponible, se debe lanzar una excepción de servicio no disponible.
3. No se puede descontar stock si la cantidad solicitada supera el stock actual del producto.
4. No se puede operar sobre un registro de inventario que no existe en la base de datos.
5. Al descontar stock, el sistema debe retornar el stock restante actualizado.

---

## Cobertura Actual

| Regla / Capa | Estado | Casos Cubiertos |
|---|---|---|
| Modelo Inventario | ✅ Cubierta | Constructor vacío, setters/getters, modificación de stock |
| Crear inventario correctamente | ✅ Cubierta | Creación exitosa con producto disponible verificado vía Feign |
| Producto no disponible al crear | ✅ Cubierta | Lanza `EstadoInvalidoException` si el producto tiene `disponible = false` |
| Producto no encontrado al crear | ✅ Cubierta | Lanza `RecursoNoEncontradoException` si Feign responde 404 |
| Servicio de productos caído | ✅ Cubierta | Lanza `ServicioNoDisponibleException` si Feign lanza excepción genérica |
| Obtener por ID existente | ✅ Cubierta | Retorna `InventarioDTO` correctamente |
| Obtener por ID inexistente | ✅ Cubierta | Lanza `RecursoNoEncontradoException` |
| Obtener por producto existente | ✅ Cubierta | Retorna `InventarioDTO` por `idProducto` |
| Obtener por producto inexistente | ✅ Cubierta | Lanza `RecursoNoEncontradoException` |
| Actualizar inventario | ✅ Cubierta | Modifica stock y persiste correctamente |
| Descontar stock exitoso | ✅ Cubierta | Descuenta cantidad y retorna stock restante |
| Descontar stock insuficiente | ✅ Cubierta | Lanza `EstadoInvalidoException` si cantidad > stock actual |
| Descontar stock producto no registrado | ✅ Cubierta | Lanza `RecursoNoEncontradoException` si no hay entrada en inventario |
| Eliminar existente | ✅ Cubierta | Llama a `repository.delete()` correctamente |
| Eliminar no existente | ✅ Cubierta | Lanza `RecursoNoEncontradoException` |
| Repositorio Inventario | ✅ Cubierta | `save`, `findById`, `findAll`, búsqueda por `idProducto` |
| Controlador Inventario | ✅ Cubierta | Respuestas HTTP 200, 201 y 404 mediante MockMvc |

---

## Clases de Test Implementadas

| Capa | Clase de Test | Herramientas |
|---|---|---|
| Modelo | `InventarioTest` | JUnit 5 |
| Servicio | `InventarioServiceTest` | JUnit 5, Mockito, `@Mock`, `@InjectMocks`, `FeignException` simulado |
| Repositorio | `InventarioRepositoryTest` | `@DataJpaTest`, H2 en memoria |
| Controlador | `InventarioControllerTest` | MockMvc, `standaloneSetup`, `GlobalExceptionHandler` |

---

## Detalle de Pruebas por Clase

### `InventarioTest` (Modelo)

| Método de test | Descripción |
|---|---|
| `testCrearInventarioConDatosValidos` | Verifica que todos los campos se asignen y recuperen correctamente |
| `testInventarioConstructorVacio` | Verifica que los campos sean `null` al instanciar sin datos |
| `testModificarStock` | Verifica que el stock se actualice correctamente al reducirlo |

### `InventarioServiceTest` (Servicio)

| Método de test | Descripción |
|---|---|
| `testListar` | Retorna lista de `InventarioDTO` y verifica que se llame al repositorio |
| `testObtenerPorIdExistente` | Retorna DTO cuando el registro existe |
| `testObtenerPorIdNoExistente` | Lanza excepción cuando el ID no existe |
| `testObtenerPorProductoExistente` | Retorna DTO al buscar por `idProducto` |
| `testObtenerPorProductoNoExistente` | Lanza excepción si el producto no tiene inventario registrado |
| `testCrearInventarioExitoso` | Crea inventario con producto disponible y persiste correctamente |
| `testCrearInventarioProductoNoDisponible` | Lanza `EstadoInvalidoException` si el producto está deshabilitado |
| `testCrearInventarioProductoNoEncontrado` | Lanza `RecursoNoEncontradoException` cuando Feign responde 404 |
| `testCrearInventarioServicioProductosCaido` | Lanza `ServicioNoDisponibleException` ante fallo genérico de Feign |
| `testActualizarInventario` | Actualiza el stock y retorna DTO con datos actualizados |
| `testDescontarStockExitoso` | Descuenta cantidad correctamente y retorna stock restante |
| `testDescontarStockInsuficiente` | Lanza `EstadoInvalidoException` si no hay stock suficiente |
| `testDescontarStockProductoNoEncontradoEnInventario` | Lanza `RecursoNoEncontradoException` si no hay registro para el producto |
| `testEliminarInventarioExitoso` | Llama a `repository.delete()` cuando el registro existe |
| `testEliminarNoExistente` | Lanza `RecursoNoEncontradoException` si el ID no existe |

### `InventarioRepositoryTest` (Repositorio)

| Método de test | Descripción |
|---|---|
| `testGuardarInventario` | Persiste un registro y verifica que se genere el ID |
| `testBuscarPorId` | Guarda y recupera un registro por su ID |
| `testListarTodos` | Guarda dos registros y verifica que `findAll()` los retorne |

### `InventarioControllerTest` (Controlador)

| Método de test | Descripción |
|---|---|
| `testListarRetorna200` | `GET /api/v2/inventario` → HTTP 200 |
| `testObtenerPorIdRetorna200` | `GET /api/v2/inventario/{id}` → HTTP 200 |
| `testObtenerPorIdNoExistenteRetorna404` | `GET /api/v2/inventario/99` → HTTP 404 con cuerpo `{"error": "NOT_FOUND"}` |
| `testObtenerPorProductoRetorna200` | `GET /api/v2/inventario/producto/{idProducto}` → HTTP 200 con datos correctos |
| `testCrearRetorna201` | `POST /api/v2/inventario` → HTTP 201 con `nombreProducto` en respuesta |
| `testDescontarStockRetorna200` | `POST /api/v2/inventario/descontar` → HTTP 200 con `stockRestante` correcto |
| `testActualizarRetorna200` | `PUT /api/v2/inventario/{id}` → HTTP 200 con `stock` actualizado |
| `testEliminarRetorna200` | `DELETE /api/v2/inventario/{id}` → HTTP 200 |

---

## Ejecución de Pruebas

```bash
mvn test
```
