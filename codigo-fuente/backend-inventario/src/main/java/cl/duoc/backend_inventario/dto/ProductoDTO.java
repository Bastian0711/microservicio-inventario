package cl.duoc.backend_inventario.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductoDTO {

    private Long idProducto;
    private String nombre;
    private BigDecimal precio;
    private String descripcion;
    private Boolean disponible;
    private Long idStand;
}