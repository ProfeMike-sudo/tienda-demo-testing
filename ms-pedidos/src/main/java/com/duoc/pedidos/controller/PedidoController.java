package com.duoc.pedidos.controller;

import com.duoc.pedidos.dto.PedidoCreateDTO;
import com.duoc.pedidos.dto.PedidoDTO;
import com.duoc.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pedidos", description = "Gestión de pedidos de la tienda")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService service;

    // ── GET /api/pedidos ───────────────────────────────────────────────────────
    @Operation(
        summary = "Listar todos los pedidos",
        description = "Retorna todos los pedidos registrados, sin importar su estado."
    )
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // ── GET /api/pedidos/{id} ──────────────────────────────────────────────────
    @Operation(summary = "Buscar pedido por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> getById(
            @Parameter(description = "ID único del pedido", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // ── POST /api/pedidos ──────────────────────────────────────────────────────
    @Operation(
        summary = "Crear nuevo pedido",
        description = "Crea un pedido consultando el catálogo de ms-productos para validar "
                    + "que el producto exista y calcular el total."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado en el catálogo"),
        @ApiResponse(responseCode = "503", description = "Servicio de productos no disponible")
    })
    @PostMapping
    public ResponseEntity<PedidoDTO> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "ID del producto y cantidad solicitada"
            )
            @Valid @RequestBody PedidoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    // ── PATCH /api/pedidos/{id}/cancelar ──────────────────────────────────────
    @Operation(
        summary = "Cancelar un pedido",
        description = "Cambia el estado del pedido a CANCELADO. Solo aplica a pedidos en estado PENDIENTE."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido cancelado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoDTO> cancelar(
            @Parameter(description = "ID del pedido a cancelar", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }
}
