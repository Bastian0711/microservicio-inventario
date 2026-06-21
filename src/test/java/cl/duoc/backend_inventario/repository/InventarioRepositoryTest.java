package cl.duoc.backend_inventario.repository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import cl.duoc.backend_inventario.model.Inventario;

@DataJpaTest
class InventarioRepositoryTest {

    @Autowired
    private InventarioRepository repository;

    @Test
    void testGuardarInventario() {
        Inventario inv = new Inventario();
        inv.setIdProducto(1L);
        inv.setNombreProducto("Sushi Combo");
        inv.setCategoria("Japonesa");
        inv.setStock(20);
        inv.setPrecio(8500.0);

        Inventario guardado = repository.save(inv);

        assertNotNull(guardado.getId());
        assertEquals("Sushi Combo", guardado.getNombreProducto());
    }

    @Test
    void testBuscarPorId() {
        Inventario inv = new Inventario();
        inv.setIdProducto(2L);
        inv.setNombreProducto("Hotdog Clásico");
        inv.setCategoria("Comida Rápida");
        inv.setStock(50);
        inv.setPrecio(3500.0);

        Inventario guardado = repository.save(inv);
        Optional<Inventario> encontrado = repository.findById(guardado.getId());

        assertTrue(encontrado.isPresent());
        assertEquals("Hotdog Clásico", encontrado.get().getNombreProducto());
    }

    @Test
    void testListarTodos() {
        Inventario i1 = new Inventario();
        i1.setIdProducto(1L);
        i1.setNombreProducto("Waffle con Nutella");
        i1.setCategoria("Postres");
        i1.setStock(30);
        i1.setPrecio(4500.0);

        Inventario i2 = new Inventario();
        i2.setIdProducto(2L);
        i2.setNombreProducto("Helado de Vainilla");
        i2.setCategoria("Postres");
        i2.setStock(40);
        i2.setPrecio(2500.0);

        repository.save(i1);
        repository.save(i2);

        List<Inventario> lista = repository.findAll();

        assertTrue(lista.size() >= 2);
    }
}