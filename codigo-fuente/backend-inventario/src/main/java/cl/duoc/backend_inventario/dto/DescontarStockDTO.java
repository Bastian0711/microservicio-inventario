package cl.duoc.backend_inventario.dto;
import lombok.Data;

@Data
public class DescontarStockDTO {

    private Long idProducto;
    private Integer cantidad;
}