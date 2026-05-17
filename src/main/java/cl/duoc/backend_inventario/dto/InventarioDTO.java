package cl.duoc.backend_inventario.dto;

import lombok.Data;

@Data
public class InventarioDTO {

    private Long id;
    private Long idProducto;
    private Integer stock;
}