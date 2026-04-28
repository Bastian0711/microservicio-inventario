package cl.duoc.backend_inventario.controller;

import cl.duoc.backend_inventario.model.Inventario;
import cl.duoc.backend_inventario.service.InventarioService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public List<Inventario> listar() {
        return inventarioService.listar();
    }

    @GetMapping("/{id}")
    public Inventario obtener(@PathVariable Long id) {
        return inventarioService.obtenerPorId(id).orElse(null);
    }

    @PostMapping
    public Inventario crear(@Valid @RequestBody Inventario inventario) {
        return inventarioService.guardar(inventario);
    }

    @PutMapping("/{id}")
    public Inventario actualizar(@PathVariable Long id, @Valid @RequestBody Inventario inventario) {
        return inventarioService.actualizar(id, inventario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.ok("Producto eliminado correctamente");
    }
}