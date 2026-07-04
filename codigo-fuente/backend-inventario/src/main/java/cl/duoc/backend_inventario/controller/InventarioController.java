package cl.duoc.backend_inventario.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.backend_inventario.dto.DescontarStockDTO;
import cl.duoc.backend_inventario.dto.InventarioCreateDTO;
import cl.duoc.backend_inventario.dto.InventarioDTO;
import cl.duoc.backend_inventario.dto.InventarioUpdateDTO;
import cl.duoc.backend_inventario.dto.StockResponseDTO;
import cl.duoc.backend_inventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Inventario", description = "Operaciones de gestión de inventario")
@RestController
@RequestMapping("/api/v3/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @Operation(summary = "Listar el inventario", description = "Retorna la lista completa del inventario.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public List<InventarioDTO> listar() {
        return inventarioService.listar();
    }

    @Operation(summary = "Buscar producto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public InventarioDTO obtener(
            @Parameter(description = "ID único del inventario", required = true)
            @PathVariable Long id) {
        return inventarioService.obtenerPorId(id);
    }

    @Operation(summary = "Buscar inventario por ID de producto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventario encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en inventario")
    })
    @GetMapping("/producto/{idProducto}")
    public InventarioDTO obtenerPorProducto(
            @Parameter(description = "ID del producto a buscar en inventario", required = true)
            @PathVariable Long idProducto) {
        return inventarioService.obtenerPorProducto(idProducto);
    }

    @Operation(summary = "Registrar nuevo inventario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Inventario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<InventarioDTO> crear(@Valid @RequestBody InventarioCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crearInventario(dto));
    }

    @Operation(summary = "Descontar stock de un producto",
               description = "Reduce la cantidad en inventario según el DTO recibido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock descontado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente o datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en inventario")
    })
    @PostMapping("/descontar")
    public StockResponseDTO descontarStock(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos del producto y cantidad a descontar", required = true)
            @RequestBody DescontarStockDTO dto) {
        return inventarioService.descontarStock(dto);
    }

    @Operation(summary = "Actualizar inventario existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Actualización exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado")
    })
    @PutMapping("/{id}")
    public InventarioDTO actualizar(
            @Parameter(description = "ID del inventario a actualizar", required = true)
            @PathVariable Long id,
            @Valid @RequestBody InventarioUpdateDTO dto) {
        return inventarioService.actualizar(id, dto);
    }

    @Operation(summary = "Eliminar inventario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Eliminación exitosa"),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID del inventario a eliminar", required = true)
            @PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.ok("Inventario eliminado correctamente");
    }
}