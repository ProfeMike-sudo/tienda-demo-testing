package com.duoc.productos.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    @Test
    @DisplayName("Constructor vacío - debe crear una instancia no nula")
    void constructorVacioDebeCrearInstanciaNoNula() {
        Producto producto = new Producto();
        assertNotNull(producto);
    }

    @Test
    @DisplayName("Constructor completo - debe asignar todos los campos correctamente")
    void constructorCompletoDebeAsignarTodosLosCampos() {
        Producto producto = new Producto(
            1L, "Laptop Lenovo", "Laptop 15.6\" Intel Core i5",
            new BigDecimal("649990.00"), 10
        );

        assertEquals(1L, producto.getId());
        assertEquals("Laptop Lenovo", producto.getNombre());
        assertEquals("Laptop 15.6\" Intel Core i5", producto.getDescripcion());
        assertEquals(new BigDecimal("649990.00"), producto.getPrecio());
        assertEquals(10, producto.getStock());
    }

    @Test
    @DisplayName("Setters - debe permitir modificar cada campo individualmente")
    void settersDebenPermitirModificarCampos() {
        Producto producto = new Producto();

        producto.setId(2L);
        producto.setNombre("Mouse Logitech MX Master 3");
        producto.setDescripcion("Mouse inalámbrico ergonómico");
        producto.setPrecio(new BigDecimal("89990.00"));
        producto.setStock(25);

        assertEquals(2L, producto.getId());
        assertEquals("Mouse Logitech MX Master 3", producto.getNombre());
        assertEquals("Mouse inalámbrico ergonómico", producto.getDescripcion());
        assertEquals(new BigDecimal("89990.00"), producto.getPrecio());
        assertEquals(25, producto.getStock());
    }

    @Test
    @DisplayName("equals y hashCode - dos productos con los mismos datos deben ser iguales")
    void dosProductosConMismosDatosDebenSerIguales() {
        Producto p1 = new Producto(1L, "Laptop Lenovo", "Laptop 15.6\"",
            new BigDecimal("649990.00"), 10);
        Producto p2 = new Producto(1L, "Laptop Lenovo", "Laptop 15.6\"",
            new BigDecimal("649990.00"), 10);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    @DisplayName("toString - debe contener el nombre del producto en la representación")
    void toStringDebeContenerNombreDelProducto() {
        Producto producto = new Producto(3L, "Teclado Mecánico Redragon", "Teclado RGB",
            new BigDecimal("59990.00"), 15);

        String texto = producto.toString();

        assertNotNull(texto);
        assertTrue(texto.contains("Teclado Mecánico Redragon"));
    }
}
