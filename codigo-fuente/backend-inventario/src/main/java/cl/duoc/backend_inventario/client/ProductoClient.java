package cl.duoc.backend_inventario.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.duoc.backend_inventario.dto.ProductoDTO;

@FeignClient(name = "producto-service", url = "${producto.service.url}")
public interface ProductoClient {

    @GetMapping("/api/v3/productos/{id}")
    ProductoDTO obtenerProducto(@PathVariable("id") Long id);
}
