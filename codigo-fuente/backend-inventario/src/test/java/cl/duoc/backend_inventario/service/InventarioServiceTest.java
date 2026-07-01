package cl.duoc.backend_inventario.service;

import java.math.BigDecimal;
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
import cl.duoc.backend_inventario.dto.DescontarStockDTO;
import cl.duoc.backend_inventario.dto.InventarioCreateDTO;
import cl.duoc.backend_inventario.dto.InventarioDTO;
import cl.duoc.backend_inventario.dto.InventarioUpdateDTO;
import cl.duoc.backend_inventario.dto.ProductoDTO;
import cl.duoc.backend_inventario.dto.StockResponseDTO;
import cl.duoc.backend_inventario.exception.EstadoInvalidoException;
import cl.duoc.backend_inventario.exception.RecursoNoEncontradoException;
import cl.duoc.backend_inventario.exception.ServicioNoDisponibleException;
import cl.duoc.backend_inventario.model.Inventario;
import cl.duoc.backend_inventario.repository.InventarioRepository;
import feign.FeignException;

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

    @Test
    void testCrearInventarioExitoso() {
        InventarioCreateDTO dto = new InventarioCreateDTO();
        dto.setIdProducto(1L);
        dto.setNombreProducto("Sushi Combo");
        dto.setCategoria("Japonesa");
        dto.setStock(20);
        dto.setPrecio(8500.0);

        ProductoDTO producto = new ProductoDTO();
        producto.setIdProducto(1L);
        producto.setNombre("Sushi Combo");
        producto.setPrecio(BigDecimal.valueOf(8500));
        producto.setDisponible(true);

        Inventario guardado = new Inventario();
        guardado.setId(1L);
        guardado.setIdProducto(1L);
        guardado.setNombreProducto("Sushi Combo");
        guardado.setCategoria("Japonesa");
        guardado.setStock(20);
        guardado.setPrecio(8500.0);

        when(productoClient.obtenerProducto(1L)).thenReturn(producto);
        when(repository.save(any(Inventario.class))).thenReturn(guardado);

        InventarioDTO resultado = inventarioService.crearInventario(dto);

        assertNotNull(resultado);
        assertEquals("Sushi Combo", resultado.getNombreProducto());
        verify(repository, times(1)).save(any(Inventario.class));
    }

    @Test
    void testCrearInventarioProductoNoDisponible() {
        InventarioCreateDTO dto = new InventarioCreateDTO();
        dto.setIdProducto(1L);
        dto.setNombreProducto("Sushi Combo");
        dto.setCategoria("Japonesa");
        dto.setStock(20);
        dto.setPrecio(8500.0);

        ProductoDTO producto = new ProductoDTO();
        producto.setIdProducto(1L);
        producto.setNombre("Sushi Combo");
        producto.setDisponible(false);

        when(productoClient.obtenerProducto(1L)).thenReturn(producto);

        assertThrows(EstadoInvalidoException.class,
                () -> inventarioService.crearInventario(dto));
    }

    @Test
    void testCrearInventarioProductoNoEncontrado() {
        InventarioCreateDTO dto = new InventarioCreateDTO();
        dto.setIdProducto(99L);
        dto.setNombreProducto("Producto Inexistente");
        dto.setCategoria("Otros");
        dto.setStock(5);
        dto.setPrecio(1000.0);

        FeignException.NotFound notFound = org.mockito.Mockito.mock(FeignException.NotFound.class);

        when(productoClient.obtenerProducto(99L)).thenThrow(notFound);

        assertThrows(RecursoNoEncontradoException.class,
                () -> inventarioService.crearInventario(dto));
    }

    @Test
    void testCrearInventarioServicioProductosCaido() {
        InventarioCreateDTO dto = new InventarioCreateDTO();
        dto.setIdProducto(1L);
        dto.setNombreProducto("Sushi Combo");
        dto.setCategoria("Japonesa");
        dto.setStock(20);
        dto.setPrecio(8500.0);

        FeignException serviceDown = org.mockito.Mockito.mock(FeignException.class);

        when(productoClient.obtenerProducto(1L)).thenThrow(serviceDown);

        assertThrows(ServicioNoDisponibleException.class,
                () -> inventarioService.crearInventario(dto));
    }

    @Test
    void testDescontarStockExitoso() {
        DescontarStockDTO dto = new DescontarStockDTO();
        dto.setIdProducto(1L);
        dto.setCantidad(5);

        ProductoDTO producto = new ProductoDTO();
        producto.setIdProducto(1L);
        producto.setDisponible(true);

        Inventario inv = new Inventario();
        inv.setId(1L);
        inv.setIdProducto(1L);
        inv.setNombreProducto("Sushi Combo");
        inv.setCategoria("Japonesa");
        inv.setStock(20);
        inv.setPrecio(8500.0);

        when(productoClient.obtenerProducto(1L)).thenReturn(producto);
        when(repository.findByIdProducto(1L)).thenReturn(Optional.of(inv));
        when(repository.save(any(Inventario.class))).thenReturn(inv);

        StockResponseDTO resultado = inventarioService.descontarStock(dto);

        assertNotNull(resultado);
        assertEquals(15, resultado.getStockRestante());
    }

    @Test
    void testDescontarStockInsuficiente() {
        DescontarStockDTO dto = new DescontarStockDTO();
        dto.setIdProducto(1L);
        dto.setCantidad(100);

        ProductoDTO producto = new ProductoDTO();
        producto.setIdProducto(1L);
        producto.setDisponible(true);

        Inventario inv = new Inventario();
        inv.setId(1L);
        inv.setIdProducto(1L);
        inv.setNombreProducto("Sushi Combo");
        inv.setCategoria("Japonesa");
        inv.setStock(20);
        inv.setPrecio(8500.0);

        when(productoClient.obtenerProducto(1L)).thenReturn(producto);
        when(repository.findByIdProducto(1L)).thenReturn(Optional.of(inv));

        assertThrows(EstadoInvalidoException.class,
                () -> inventarioService.descontarStock(dto));
    }

    @Test
    void testDescontarStockProductoNoEncontradoEnInventario() {
        DescontarStockDTO dto = new DescontarStockDTO();
        dto.setIdProducto(99L);
        dto.setCantidad(1);

        ProductoDTO producto = new ProductoDTO();
        producto.setIdProducto(99L);
        producto.setDisponible(true);

        when(productoClient.obtenerProducto(99L)).thenReturn(producto);
        when(repository.findByIdProducto(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> inventarioService.descontarStock(dto));
    }

    @Test
    void testObtenerPorProductoExistente() {
        Inventario inv = new Inventario();
        inv.setId(1L);
        inv.setIdProducto(1L);
        inv.setNombreProducto("Sushi Combo");
        inv.setCategoria("Japonesa");
        inv.setStock(20);
        inv.setPrecio(8500.0);

        when(repository.findByIdProducto(1L)).thenReturn(Optional.of(inv));

        InventarioDTO resultado = inventarioService.obtenerPorProducto(1L);

        assertNotNull(resultado);
        assertEquals("Sushi Combo", resultado.getNombreProducto());
    }

    @Test
    void testEliminarInventarioExitoso() {
        Inventario inv = new Inventario();
        inv.setId(1L);
        inv.setIdProducto(1L);
        inv.setNombreProducto("Sushi Combo");
        inv.setCategoria("Japonesa");
        inv.setStock(20);
        inv.setPrecio(8500.0);

        when(repository.findById(1L)).thenReturn(Optional.of(inv));

        inventarioService.eliminar(1L);

        verify(repository, times(1)).delete(inv);
    }
}