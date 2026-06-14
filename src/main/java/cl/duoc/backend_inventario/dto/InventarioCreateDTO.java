package cl.duoc.backend_inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para registrar un producto en inventario")
public class InventarioCreateDTO {

    @Schema(description = "ID del producto en el catálogo", example = "1")
    @NotNull(message = "El id del producto es obligatorio")
    @Positive(message = "El id del producto debe ser mayor a cero")
    private Long idProducto;

    @Schema(description = "Nombre del producto", example = "Teclado mecánico")
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombreProducto;

    @Schema(description = "Categoría del producto", example = "Periféricos")
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @Schema(description = "Cantidad disponible en stock", example = "50")
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Schema(description = "Precio unitario del producto", example = "29990.0")
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private Double precio;
}