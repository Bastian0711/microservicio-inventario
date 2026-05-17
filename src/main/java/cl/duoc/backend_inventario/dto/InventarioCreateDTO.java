package cl.duoc.backend_inventario.dto;

import lombok.Data;

@Data
public class InventarioCreateDTO {

    private Long idProducto;
    private String nombreProducto;
    private String categoria;
    private Integer stock;
    private Double precio;
}