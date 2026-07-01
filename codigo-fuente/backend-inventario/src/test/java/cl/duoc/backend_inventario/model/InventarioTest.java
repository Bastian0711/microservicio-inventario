package cl.duoc.backend_inventario.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class InventarioTest {

    @Test
    void testCrearInventarioConDatosValidos() {
        Inventario inv = new Inventario();
        inv.setId(1L);
        inv.setIdProducto(10L);
        inv.setNombreProducto("Sushi Combo");
        inv.setCategoria("Japonesa");
        inv.setStock(20);
        inv.setPrecio(8500.0);

        assertEquals(1L, inv.getId());
        assertEquals(10L, inv.getIdProducto());
        assertEquals("Sushi Combo", inv.getNombreProducto());
        assertEquals("Japonesa", inv.getCategoria());
        assertEquals(20, inv.getStock());
        assertEquals(8500.0, inv.getPrecio());
    }

    @Test
    void testInventarioConstructorVacio() {
        Inventario inv = new Inventario();

        assertNull(inv.getId());
        assertNull(inv.getNombreProducto());
        assertNull(inv.getStock());
    }

    @Test
    void testModificarStock() {
        Inventario inv = new Inventario();
        inv.setNombreProducto("Hotdog Clásico");
        inv.setStock(50);
        inv.setStock(inv.getStock() - 5);

        assertEquals(45, inv.getStock());
    }
}