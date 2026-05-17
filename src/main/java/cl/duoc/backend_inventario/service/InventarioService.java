package cl.duoc.backend_inventario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.duoc.backend_inventario.client.ProductoClient;
import cl.duoc.backend_inventario.dto.DescontarStockDTO;
import cl.duoc.backend_inventario.dto.InventarioCreateDTO;
import cl.duoc.backend_inventario.dto.InventarioDTO;
import cl.duoc.backend_inventario.dto.InventarioUpdateDTO;
import cl.duoc.backend_inventario.dto.ProductoDTO;
import cl.duoc.backend_inventario.dto.StockResponseDTO;
import cl.duoc.backend_inventario.exception.RecursoNoEncontradoException;
import cl.duoc.backend_inventario.exception.ServicioNoDisponibleException;
import cl.duoc.backend_inventario.model.Inventario;
import cl.duoc.backend_inventario.repository.InventarioRepository;
import feign.FeignException;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoClient productoClient;

    public InventarioService(InventarioRepository inventarioRepository,
            ProductoClient productoClient) {
        this.inventarioRepository = inventarioRepository;
        this.productoClient = productoClient;
    }

    private ProductoDTO validarProducto(Long idProducto) {
        try {
            ProductoDTO producto = productoClient.obtenerProducto(idProducto);

            if (producto == null) {
                throw new RecursoNoEncontradoException("Producto no encontrado");
            }

            if (Boolean.FALSE.equals(producto.getDisponible())) {
                throw new RecursoNoEncontradoException("Producto no disponible");
            }

            return producto;

        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("Producto no encontrado");
        } catch (FeignException e) {
            throw new ServicioNoDisponibleException("No se pudo consultar el microservicio de productos");
        }
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

    public InventarioDTO crearInventario(InventarioCreateDTO dto) {

        validarProducto(dto.getIdProducto());

        Inventario inventario = new Inventario();

        inventario.setIdProducto(dto.getIdProducto());
        inventario.setNombreProducto(dto.getNombreProducto());
        inventario.setCategoria(dto.getCategoria());
        inventario.setStock(dto.getStock());
        inventario.setPrecio(dto.getPrecio());

        Inventario inventarioGuardado = inventarioRepository.save(inventario);

        InventarioDTO response = new InventarioDTO();
        response.setId(inventarioGuardado.getId());
        response.setIdProducto(inventarioGuardado.getIdProducto());
        response.setStock(inventarioGuardado.getStock());

        return response;
    }

    public InventarioDTO actualizar(Long id, InventarioUpdateDTO dto) {

        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado"));

        inventario.setStock(dto.getStock());

        Inventario inventarioActualizado = inventarioRepository.save(inventario);

        InventarioDTO response = new InventarioDTO();
        response.setId(inventarioActualizado.getId());
        response.setIdProducto(inventarioActualizado.getIdProducto());
        response.setStock(inventarioActualizado.getStock());

        return response;
    }

    public void eliminar(Long id) {
        inventarioRepository.deleteById(id);
    }

    public Inventario obtenerPorProducto(Long idProducto) {
        return inventarioRepository.findAll()
                .stream()
                .filter(inv -> inv.getIdProducto().equals(idProducto))
                .findFirst()
                .orElse(null);
    }

    public StockResponseDTO descontarStock(DescontarStockDTO dto) {

        validarProducto(dto.getIdProducto());

        Inventario inventario = obtenerPorProducto(dto.getIdProducto());

        if (inventario == null) {
            throw new RecursoNoEncontradoException("Inventario no encontrado para el producto");
        }

        if (inventario.getStock() < dto.getCantidad()) {
            throw new RecursoNoEncontradoException("Stock insuficiente");
        }

        inventario.setStock(inventario.getStock() - dto.getCantidad());
        inventarioRepository.save(inventario);

        StockResponseDTO response = new StockResponseDTO();
        response.setStockRestante(inventario.getStock());

        return response;
    }
}
