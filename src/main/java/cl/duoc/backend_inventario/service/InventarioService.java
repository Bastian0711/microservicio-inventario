package cl.duoc.backend_inventario.service;

import cl.duoc.backend_inventario.model.Inventario;
import cl.duoc.backend_inventario.repository.InventarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public List<Inventario> listar() {
        return inventarioRepository.findAll();
    }

    public Optional<Inventario> obtenerPorId(Long id) {
        return inventarioRepository.findById(id);
    }

    public Inventario guardar(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    public Inventario actualizar(Long id, Inventario nuevo) {
        return inventarioRepository.findById(id).map(inv -> {
            inv.setNombreProducto(nuevo.getNombreProducto());
            inv.setCategoria(nuevo.getCategoria());
            inv.setStock(nuevo.getStock());
            inv.setPrecio(nuevo.getPrecio());
            return inventarioRepository.save(inv);
        }).orElse(null);
    }

    public void eliminar(Long id) {
        inventarioRepository.deleteById(id);
    }
}