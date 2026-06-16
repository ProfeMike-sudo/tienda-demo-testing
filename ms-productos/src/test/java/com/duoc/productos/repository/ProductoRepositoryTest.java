package com.duoc.productos.repository;

import com.duoc.productos.model.Producto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository repository;

    @Test
    @DisplayName("save - debe persistir el producto y asignar un ID generado automáticamente")
    void debePersistirProductoYAsignarIdGenerado() {
        // Given
        Producto producto = new Producto(null, "Teclado Mecánico Redragon",
            "Teclado RGB switches Blue", new BigDecimal("59990.00"), 15);

        // When
        Producto guardado = repository.save(producto);

        // Then
        assertNotNull(guardado.getId());
        assertTrue(guardado.getId() > 0);
        assertEquals("Teclado Mecánico Redragon", guardado.getNombre());
        assertEquals(15, guardado.getStock());
    }

    @Test
    @DisplayName("findAll - debe retornar todos los productos guardados en la BD")
    void debeRetornarTodosLosProductosGuardados() {
        // Given
        repository.save(new Producto(null, "Monitor Samsung 24\"", "Monitor Full HD 1080p",
            new BigDecimal("199990.00"), 8));
        repository.save(new Producto(null, "Webcam Logitech C920", "Cámara web Full HD",
            new BigDecimal("79990.00"), 20));

        // When
        List<Producto> productos = repository.findAll();

        // Then
        assertNotNull(productos);
        assertEquals(2, productos.size());
    }

    @Test
    @DisplayName("findById - debe retornar el producto correcto cuando el ID existe")
    void debeEncontrarProductoPorIdExistente() {
        // Given
        Producto guardado = repository.save(
            new Producto(null, "Audífonos Sony WH-1000XM5",
                "Audífonos ANC 30h batería", new BigDecimal("289990.00"), 12)
        );

        // When
        Optional<Producto> resultado = repository.findById(guardado.getId());

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Audífonos Sony WH-1000XM5", resultado.get().getNombre());
        assertEquals(new BigDecimal("289990.00"), resultado.get().getPrecio());
    }

    @Test
    @DisplayName("findById - debe retornar Optional vacío cuando el ID no existe")
    void debeRetornarOptionalVacioCuandoIdNoExiste() {
        // When
        Optional<Producto> resultado = repository.findById(999L);

        // Then
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("deleteById - debe eliminar el producto de la base de datos")
    void debeEliminarProductoPorId() {
        // Given
        Producto guardado = repository.save(
            new Producto(null, "Laptop HP Pavilion", "Laptop 14\" Intel Core i7",
                new BigDecimal("499990.00"), 5)
        );
        Long id = guardado.getId();

        // When
        repository.deleteById(id);

        // Then
        assertFalse(repository.findById(id).isPresent());
    }
}
