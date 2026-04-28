package cl.duoc.backend_inventario.repository;

import cl.duoc.backend_inventario.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
}