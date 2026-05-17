package cl.duoc.backend_inventario.controller;

import java.util.List;

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
import cl.duoc.backend_inventario.model.Inventario;
import cl.duoc.backend_inventario.service.InventarioService;

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

    @GetMapping("/producto/{idProducto}")
    public Inventario obtenerPorProducto(@PathVariable Long idProducto) {
        return inventarioService.obtenerPorProducto(idProducto);
    }

    @PostMapping
    public InventarioDTO crear(@RequestBody InventarioCreateDTO dto) {
        return inventarioService.crearInventario(dto);
    }

    @PostMapping("/descontar")
    public StockResponseDTO descontarStock(@RequestBody DescontarStockDTO dto) {
        return inventarioService.descontarStock(dto);
    }

    @PutMapping("/{id}")
    public InventarioDTO actualizar(@PathVariable Long id, @RequestBody InventarioUpdateDTO dto) {
        return inventarioService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.ok("Producto eliminado correctamente");
    }
}
