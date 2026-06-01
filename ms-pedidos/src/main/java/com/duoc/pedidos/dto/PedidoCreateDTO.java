package com.duoc.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Schema(description = "Datos para registrar un nuevo pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCreateDTO {

    @Schema(description = "ID del producto a pedir (debe existir en ms-productos)", example = "1")
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @Schema(description = "Cantidad de unidades solicitadas", example = "3")
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
}
