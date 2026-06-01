package com.duoc.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Schema(description = "Datos necesarios para crear o actualizar un producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoCreateDTO {

    @Schema(description = "Nombre del producto", example = "Audífonos Bluetooth")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    private String nombre;

    @Schema(description = "Descripción detallada del producto", example = "Audífonos inalámbricos con cancelación de ruido")
    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String descripcion;

    @Schema(description = "Precio unitario del producto en pesos", example = "49990.00")
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @Schema(description = "Cantidad disponible en inventario", example = "25")
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
}
