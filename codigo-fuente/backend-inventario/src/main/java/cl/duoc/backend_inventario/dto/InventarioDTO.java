package cl.duoc.backend_inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioDTO {
    private Long id;
    private Long idProducto;
    private String nombreProducto;
    private String categoria;
    private Integer stock;
    private Double precio;
}
