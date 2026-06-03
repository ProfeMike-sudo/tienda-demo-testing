package com.duoc.productos.service;

import com.duoc.productos.dto.ProductoCreateDTO;
import com.duoc.productos.dto.ProductoDTO;
import com.duoc.productos.exception.RecursoNoEncontradoException;
import com.duoc.productos.model.Producto;
import com.duoc.productos.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService productoService;

    // ── findAll ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll - debe retornar lista de productos cuando existen registros")
    void debeRetornarListaDeProductos() {
        // Given
        List<Producto> productosSimulados = List.of(
            new Producto(1L, "Laptop Lenovo", "Laptop 15.6\"", new BigDecimal("649990.00"), 10),
            new Producto(2L, "Mouse Logitech", "Mouse inalámbrico", new BigDecimal("89990.00"), 25)
        );
        when(repository.findAll()).thenReturn(productosSimulados);

        // When
        List<ProductoDTO> resultado = productoService.findAll();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Laptop Lenovo", resultado.get(0).getNombre());
        assertEquals(new BigDecimal("649990.00"), resultado.get(0).getPrecio());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll - debe retornar lista vacía cuando no hay productos")
    void debeRetornarListaVaciaSiNoHayProductos() {
        // Given
        when(repository.findAll()).thenReturn(List.of());

        // When
        List<ProductoDTO> resultado = productoService.findAll();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ── findById ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById - debe retornar el DTO correcto cuando el producto existe")
    void debeRetornarProductoPorId() {
        // Given
        Producto producto = new Producto(1L, "Laptop Lenovo", "Laptop 15.6\"", new BigDecimal("649990.00"), 10);
        when(repository.findById(1L)).thenReturn(Optional.of(producto));

        // When
        ProductoDTO resultado = productoService.findById(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Laptop Lenovo", resultado.getNombre());
        assertEquals(10, resultado.getStock());
    }

    @Test
    @DisplayName("findById - debe lanzar RecursoNoEncontradoException cuando el ID no existe")
    void debeLanzarExcepcionCuandoProductoNoExiste() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () ->
            productoService.findById(999L)
        );
    }

    // ── crear ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("crear - debe persistir y retornar el producto con ID generado")
    void debeCrearProductoCorrectamente() {
        // Given
        ProductoCreateDTO dto = new ProductoCreateDTO(
            "Teclado Mecánico", "Teclado RGB switches Blue",
            new BigDecimal("59990.00"), 15
        );
        Producto guardado = new Producto(3L, "Teclado Mecánico", "Teclado RGB switches Blue",
            new BigDecimal("59990.00"), 15);
        when(repository.save(any(Producto.class))).thenReturn(guardado);

        // When
        ProductoDTO resultado = productoService.crear(dto);

        // Then
        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals("Teclado Mecánico", resultado.getNombre());
        assertEquals(new BigDecimal("59990.00"), resultado.getPrecio());
        verify(repository, times(1)).save(any(Producto.class));
    }

    // ── eliminar ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar - debe lanzar excepción al intentar eliminar un ID inexistente")
    void debeLanzarExcepcionAlEliminarProductoInexistente() {
        // Given
        when(repository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () ->
            productoService.eliminar(999L)
        );
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("eliminar - debe invocar deleteById cuando el producto existe")
    void debeEliminarProductoExistente() {
        // Given
        when(repository.existsById(1L)).thenReturn(true);

        // When
        productoService.eliminar(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
    }
}
