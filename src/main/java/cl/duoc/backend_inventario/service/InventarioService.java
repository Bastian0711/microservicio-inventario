package cl.duoc.backend_inventario.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

@Service
public class InventarioService {

    private static final Logger log =
            LoggerFactory.getLogger(InventarioService.class);

    private final InventarioRepository repository;
    private final ProductoClient productoClient;

    public InventarioService(
            InventarioRepository repository,
            ProductoClient productoClient) {

        this.repository = repository;
        this.productoClient = productoClient;
    }

    public List<InventarioDTO> listar() {

        return repository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public InventarioDTO obtenerPorId(Long id) {
        return toDto(obtenerEntidadPorId(id));
    }

    public InventarioDTO obtenerPorProducto(Long idProducto) {

        Inventario inventario = repository.findByIdProducto(idProducto)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Inventario no encontrado para el producto"));

        return toDto(inventario);
    }

    public InventarioDTO crearInventario(InventarioCreateDTO dto) {

        ProductoDTO producto = validarProducto(dto.getIdProducto());

        log.info("Creando inventario para producto id={}, nombre={}",
                dto.getIdProducto(), producto.getNombre());

        Inventario inventario = new Inventario();

        inventario.setIdProducto(dto.getIdProducto());
        inventario.setNombreProducto(dto.getNombreProducto());
        inventario.setCategoria(dto.getCategoria());
        inventario.setStock(dto.getStock());
        inventario.setPrecio(dto.getPrecio());

        Inventario guardado = repository.save(inventario);

        log.info("Inventario creado exitosamente id={}", guardado.getId());

        return toDto(guardado);
    }

    public InventarioDTO actualizar(Long id, InventarioUpdateDTO dto) {

        Inventario inventario = obtenerEntidadPorId(id);

        inventario.setStock(dto.getStock());

        return toDto(repository.save(inventario));
    }

    public void eliminar(Long id) {

        Inventario inventario = obtenerEntidadPorId(id);

        repository.delete(inventario);

        log.info("Inventario eliminado id={}", id);
    }

    public StockResponseDTO descontarStock(DescontarStockDTO dto) {

        validarProducto(dto.getIdProducto());

        Inventario inventario = repository.findByIdProducto(dto.getIdProducto())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Inventario no encontrado para el producto"));

        if (inventario.getStock() < dto.getCantidad()) {

            throw new EstadoInvalidoException("Stock insuficiente para el producto");
        }

        inventario.setStock(inventario.getStock() - dto.getCantidad());

        repository.save(inventario);

        log.info("Stock descontado: producto={}, cantidad={}, restante={}",
                dto.getIdProducto(), dto.getCantidad(), inventario.getStock());

        return new StockResponseDTO(inventario.getStock());
    }

    private ProductoDTO validarProducto(Long idProducto) {

        try {

            log.info("Consultando producto id={}", idProducto);

            ProductoDTO producto = productoClient.obtenerProducto(idProducto);

            log.info("Producto encontrado: {}", producto.getNombre());

            if (Boolean.FALSE.equals(producto.getDisponible())) {

                throw new EstadoInvalidoException("Producto no disponible");
            }

            return producto;

        } catch (EstadoInvalidoException e) {

            throw e;

        } catch (FeignException.NotFound e) {

            log.warn("Producto id={} no existe", idProducto);

            throw new RecursoNoEncontradoException("Producto no encontrado");

        } catch (FeignException e) {

            log.error("Error al consultar servicio Productos: {}", e.getMessage());

            throw new ServicioNoDisponibleException(
                    "Servicio de productos no disponible");
        }
    }

    private Inventario obtenerEntidadPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Inventario no encontrado"));
    }

    private InventarioDTO toDto(Inventario i) {

        return new InventarioDTO(
                i.getId(),
                i.getIdProducto(),
                i.getNombreProducto(),
                i.getCategoria(),
                i.getStock(),
                i.getPrecio()
        );
    }
}
