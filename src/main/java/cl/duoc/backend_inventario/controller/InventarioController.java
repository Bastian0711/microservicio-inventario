package cl.duoc.backend_inventario.controller;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.backend_inventario.dto.DescontarStockDTO;
import cl.duoc.backend_inventario.dto.InventarioCreateDTO;
import cl.duoc.backend_inventario.dto.InventarioDTO;
import cl.duoc.backend_inventario.dto.InventarioUpdateDTO;
import cl.duoc.backend_inventario.dto.StockResponseDTO;
import cl.duoc.backend_inventario.service.InventarioService;

@RestController
@RequestMapping("/api/v2/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public List<InventarioDTO> listar() {
        return inventarioService.listar();
    }

    @GetMapping("/{id}")
    public InventarioDTO obtener(@PathVariable Long id) {
        return inventarioService.obtenerPorId(id);
    }

    @GetMapping("/producto/{idProducto}")
    public InventarioDTO obtenerPorProducto(@PathVariable Long idProducto) {
        return inventarioService.obtenerPorProducto(idProducto);
    }

    @PostMapping
    public ResponseEntity<InventarioDTO> crear(@Valid @RequestBody InventarioCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crearInventario(dto));
    }

    @PostMapping("/descontar")
    public StockResponseDTO descontarStock(@RequestBody DescontarStockDTO dto) {
        return inventarioService.descontarStock(dto);
    }

    @PutMapping("/{id}")
    public InventarioDTO actualizar(@PathVariable Long id, @Valid @RequestBody InventarioUpdateDTO dto) {
        return inventarioService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.ok("Inventario eliminado correctamente");
    }
}
