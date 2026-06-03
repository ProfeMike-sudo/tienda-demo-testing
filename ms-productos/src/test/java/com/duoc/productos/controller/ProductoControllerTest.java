package com.duoc.productos.controller;

import com.duoc.productos.dto.ProductoDTO;
import com.duoc.productos.exception.GlobalExceptionHandler;
import com.duoc.productos.exception.RecursoNoEncontradoException;
import com.duoc.productos.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoService service;

    @InjectMocks
    private ProductoController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ── GET /api/productos ─────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/productos - debe retornar 200 con la lista de productos")
    void debeRetornar200CuandoSePidenProductos() throws Exception {
        // Given
        when(service.findAll()).thenReturn(List.of(
            new ProductoDTO(1L, "Laptop Lenovo", "Laptop 15.6\"", new BigDecimal("649990.00"), 10),
            new ProductoDTO(2L, "Mouse Logitech", "Mouse inalámbrico", new BigDecimal("89990.00"), 25)
        ));

        // When & Then
        mockMvc.perform(get("/api/productos"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].nombre").value("Laptop Lenovo"))
               .andExpect(jsonPath("$[0].precio").value(649990.00));
    }

    @Test
    @DisplayName("GET /api/productos - debe retornar 200 con lista vacía cuando no hay registros")
    void debeRetornar200ConListaVacia() throws Exception {
        // Given
        when(service.findAll()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/productos"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(0));
    }

    // ── GET /api/productos/{id} ────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/productos/{id} - debe retornar 404 cuando el producto no existe")
    void debeRetornar404CuandoProductoNoExiste() throws Exception {
        // Given
        when(service.findById(999L))
            .thenThrow(new RecursoNoEncontradoException("Producto no encontrado: 999"));

        // When & Then
        mockMvc.perform(get("/api/productos/999"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("Producto no encontrado: 999"));
    }

    // ── POST /api/productos ────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/productos - debe retornar 201 al crear un producto válido")
    void debeRetornar201AlCrearProducto() throws Exception {
        // Given
        String json = """
            {
                "nombre": "Audífonos Bluetooth",
                "descripcion": "Audífonos con cancelación de ruido activa",
                "precio": 79990.00,
                "stock": 20
            }
            """;
        when(service.crear(any())).thenReturn(
            new ProductoDTO(7L, "Audífonos Bluetooth",
                "Audífonos con cancelación de ruido activa",
                new BigDecimal("79990.00"), 20)
        );

        // When & Then
        mockMvc.perform(post("/api/productos")
               .contentType(MediaType.APPLICATION_JSON)
               .content(json))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(7))
               .andExpect(jsonPath("$.nombre").value("Audífonos Bluetooth"));
    }

    @Test
    @DisplayName("POST /api/productos - debe retornar 400 cuando el nombre está en blanco")
    void debeRetornar400CuandoNombreEstaVacio() throws Exception {
        // Given — nombre vacío y precio negativo
        String json = """
            {
                "nombre": "",
                "precio": -500,
                "stock": 5
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/productos")
               .contentType(MediaType.APPLICATION_JSON)
               .content(json))
               .andExpect(status().isBadRequest());
    }
}
