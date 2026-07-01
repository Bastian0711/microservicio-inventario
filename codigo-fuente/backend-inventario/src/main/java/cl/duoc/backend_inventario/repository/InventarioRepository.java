package cl.duoc.backend_inventario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.backend_inventario.model.Inventario;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByIdProducto(Long idProducto);
}
