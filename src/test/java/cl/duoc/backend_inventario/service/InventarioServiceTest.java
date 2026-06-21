package cl.duoc.backend_inventario.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.backend_inventario.client.ProductoClient;
import cl.duoc.backend_inventario.dto.InventarioDTO;
import cl.duoc.backend_inventario.dto.InventarioUpdateDTO;
import cl.duoc.backend_inventario.exception.RecursoNoEncontradoException;
import cl.duoc.backend_inventario.model.Inventario;
import cl.duoc.backend_inventario.repository.InventarioRepository;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository repository;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private InventarioService inventarioService;

    @Test
    void testListar() {
        Inventario inv = new Inventario();
        inv.setId(1L);
        inv.setIdProducto(1L);
        inv.setNombreProducto("Waffle con Nutella");
        inv.setCategoria("Postres");
        inv.setStock(30);
        inv.setPrecio(4500.0);

        when(repository.findAll()).thenReturn(List.of(inv));

        List<InventarioDTO> resultado = inventarioService.listar();

        assertEquals(1, resultado.size());
        assertEquals("Waffle con Nutella", resultado.get(0).getNombreProducto());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testObtenerPorIdNoExistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> inventarioService.obtenerPorId(99L));
    }

    @Test
    void testEliminarNoExistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> inventarioService.eliminar(99L));
    }

    @Test
    void testActualizarInventario() {
        Inventario inv = new Inventario();
        inv.setId(1L);
        inv.setIdProducto(1L);
        inv.setNombreProducto("Helado de Vainilla");
        inv.setCategoria("Postres");
        inv.setStock(40);
        inv.setPrecio(2500.0);

        InventarioUpdateDTO dto = new InventarioUpdateDTO();
        dto.setStock(35);

        when(repository.findById(1L)).thenReturn(Optional.of(inv));
        when(repository.save(any(Inventario.class))).thenReturn(inv);

        InventarioDTO resultado = inventarioService.actualizar(1L, dto);

        assertNotNull(resultado);
    }

    @Test
    void testObtenerPorProductoNoExistente() {
        when(repository.findByIdProducto(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> inventarioService.obtenerPorProducto(99L));
    }

    @Test
    void testObtenerPorIdExistente() {
        Inventario inv = new Inventario();
        inv.setId(1L);
        inv.setIdProducto(1L);
        inv.setNombreProducto("Hotdog Clásico");
        inv.setCategoria("Comida Rápida");
        inv.setStock(50);
        inv.setPrecio(3500.0);

        when(repository.findById(1L)).thenReturn(Optional.of(inv));

        InventarioDTO resultado = inventarioService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("Hotdog Clásico", resultado.getNombreProducto());
    }
}