# tienda-demo-swagger

Proyecto de referencia para la **Guía 8 — Documentación de APIs con OpenAPI y Swagger** de la asignatura DSY1103 Desarrollo Fullstack I, Duoc UC 2026-1.

Este repositorio es una versión extendida de `tienda-demo` con la implementación completa de **Swagger UI** en ambos microservicios (`ms-productos` y `ms-pedidos`). Úsalo como guía de referencia mientras realizas la actividad práctica en tu proyecto semestral.

---

## Qué hay implementado

| Microservicio | Puerto | URL de Swagger UI |
|---|---|---|
| ms-productos | 8081 | `http://localhost:8081/doc/swagger-ui.html` |
| ms-pedidos | 8082 | `http://localhost:8082/doc/swagger-ui.html` |

Ambos servicios se levantan con Docker Compose junto a sus bases de datos MySQL independientes.

---

## Paso a paso: qué se cambió y por qué

Esta sección documenta **exactamente qué archivos se modificaron o crearon** para agregar Swagger UI. Son los mismos pasos que debes replicar en tu proyecto.

---

### Paso 1 — Agregar la dependencia en `pom.xml`

**Qué se cambió:** en el `pom.xml` de cada microservicio se agrega un bloque `<dependency>` dentro de `<dependencies>`.

```xml
<!-- Swagger UI + OpenAPI 3 (Guía 8) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

**Por qué esta dependencia:**  
Incluye dos cosas a la vez: la librería `springdoc-openapi` que escanea tus controladores y genera automáticamente el JSON de especificación en `/v3/api-docs`, y la interfaz visual `Swagger UI` que lo muestra como página interactiva en el navegador. Con una sola dependencia tienes todo.

**Archivos modificados:**
- `ms-productos/pom.xml`
- `ms-pedidos/pom.xml`

> Después de guardar `pom.xml`, recarga Maven en el IDE para que descargue la librería.

---

### Paso 2 — Configurar `application.properties`

**Qué se cambió:** se agregan tres líneas al final del `application.properties` de cada microservicio.

```properties
# Swagger UI / OpenAPI
springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/doc/swagger-ui.html
```

**Qué hace cada línea:**

| Propiedad | Efecto |
|---|---|
| `api-docs.enabled=true` | Activa la generación automática del JSON OpenAPI en `/v3/api-docs` |
| `swagger-ui.enabled=true` | Activa la interfaz visual de Swagger UI |
| `swagger-ui.path=...` | Define la URL donde puedes acceder. Puedes cambiarla si lo necesitas. |

Con solo estas líneas y la dependencia del paso anterior, Swagger UI ya funciona. Los siguientes pasos mejoran cómo se ve y qué información muestra.

**Archivos modificados:**
- `ms-productos/src/main/resources/application.properties`
- `ms-pedidos/src/main/resources/application.properties`

---

### Paso 3 — Crear `SwaggerConfig.java`

**Qué se cambió:** se crea un archivo nuevo en el paquete `config` de cada microservicio.

**Ejemplo — ms-productos** (`com.duoc.productos.config.SwaggerConfig`):

```java
package com.duoc.productos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration          // Le dice a Spring que esta clase tiene configuraciones
public class SwaggerConfig {

    @Bean               // Spring ejecuta este método al iniciar y registra el resultado
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Productos Service")   // ← cambia por tu servicio
                        .version("1.0")
                        .description("Microservicio de gestión de productos de la tienda."));
    }
}
```

**Por qué es necesario:**  
Sin esta clase, Swagger UI muestra un título genérico. Con ella puedes personalizar el título, la versión y la descripción que aparecen en la cabecera de la interfaz.

**Cómo adaptarlo:**  
Cambia `.title(...)` y `.description(...)` para que reflejen el nombre real de tu microservicio. El resto del código es idéntico para cualquier servicio.

**Archivos creados:**
- `ms-productos/src/main/java/com/duoc/productos/config/SwaggerConfig.java`
- `ms-pedidos/src/main/java/com/duoc/pedidos/config/SwaggerConfig.java`

---

### Paso 4 — Anotar el controlador

**Qué se cambió:** se agregan anotaciones Swagger sobre la clase del controlador y cada uno de sus métodos. El código original no se modifica, solo se agregan las anotaciones encima.

**Anotaciones usadas:**

| Anotación | Dónde va | Qué hace |
|---|---|---|
| `@Tag` | Sobre la clase `@RestController` | Agrupa los endpoints bajo una sección con nombre en Swagger UI |
| `@Operation` | Sobre cada método del controller | Define el título corto y la descripción del endpoint |
| `@ApiResponse` / `@ApiResponses` | Sobre cada método | Documenta los posibles códigos HTTP de respuesta (200, 201, 404, etc.) |
| `@Parameter` | Sobre cada `@PathVariable` o `@RequestParam` | Describe el parámetro de la URL |
| `@RequestBody` (Swagger) | Sobre el parámetro del cuerpo en POST/PUT | Describe el cuerpo de la petición |

**Ejemplo completo — `ProductoController.java` antes y después:**

Antes (código original, sin cambios):
```java
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
```

Después (con anotaciones Swagger agregadas):
```java
@Tag(name = "Productos", description = "Operaciones CRUD del catálogo de productos")
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Operation(
        summary = "Listar todos los productos",
        description = "Retorna la lista completa de productos registrados en el catálogo."
    )
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Buscar producto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getById(
            @Parameter(description = "ID único del producto", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Crear nuevo producto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<ProductoDTO> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos del nuevo producto"
            )
            @Valid @RequestBody ProductoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }
}
```

> **Importante:** el `@RequestBody` de Swagger (`io.swagger.v3.oas.annotations.parameters.RequestBody`) es diferente del `@RequestBody` de Spring MVC. Pueden coexistir en el mismo parámetro. Por claridad, en este proyecto se escribe con el nombre de paquete completo para que no haya confusión.

**Archivos modificados:**
- `ms-productos/src/main/java/com/duoc/productos/controller/ProductoController.java`
- `ms-pedidos/src/main/java/com/duoc/pedidos/controller/PedidoController.java`

---

### Paso 5 (Bonus) — Documentar el DTO con `@Schema`

**Qué se cambió:** se agregan anotaciones `@Schema` sobre la clase DTO y cada uno de sus campos.

```java
@Schema(description = "Datos necesarios para crear o actualizar un producto")
public class ProductoCreateDTO {

    @Schema(description = "Nombre del producto", example = "Audífonos Bluetooth")
    private String nombre;

    @Schema(description = "Precio unitario del producto en pesos", example = "49990.00")
    private BigDecimal precio;

    @Schema(description = "Cantidad disponible en inventario", example = "25")
    private Integer stock;
}
```

**Por qué mejora la experiencia:**  
Cuando alguien abre Swagger UI y quiere probar el `POST /api/productos`, el formulario muestra los campos con descripciones y ejemplos reales, no vacíos. Esto hace tu API más fácil de entender y usar sin necesitar documentación adicional.

**Archivos modificados:**
- `ms-productos/src/main/java/com/duoc/productos/dto/ProductoCreateDTO.java`
- `ms-pedidos/src/main/java/com/duoc/pedidos/dto/PedidoCreateDTO.java`

---

## Cómo ejecutar el proyecto

### Requisitos
- Docker Desktop instalado y en ejecución
- Git

### Clonar y levantar

```bash
git clone https://github.com/ProfeMike-sudo/tienda-demo-swagger.git
cd tienda-demo-swagger
docker compose up --build
```

Docker construye las imágenes de ambos microservicios y levanta los cuatro contenedores:

| Contenedor | Descripción |
|---|---|
| `db-productos` | MySQL para el catálogo de productos |
| `db-pedidos` | MySQL para los pedidos |
| `ms-productos` | Microservicio proveedor (puerto 8081) |
| `ms-pedidos` | Microservicio consumidor (puerto 8082) |

La primera vez puede demorar 3-5 minutos porque descarga las imágenes base de Java y MySQL.

### Verificar que todo está corriendo

```bash
docker ps
```

Deben aparecer 4 contenedores en estado `Up`.

### Acceder a Swagger UI

Una vez que los contenedores estén corriendo, abre en el navegador:

- **ms-productos:** `http://localhost:8081/doc/swagger-ui.html`
- **ms-pedidos:** `http://localhost:8082/doc/swagger-ui.html`

### Probar desde Swagger UI

1. Haz clic en cualquier endpoint (por ejemplo `GET /api/productos`).
2. Haz clic en **"Try it out"**.
3. Haz clic en **"Execute"**.
4. Verifica que la respuesta tenga código **200** y un JSON en el campo *Response body*.

Para crear un producto: expande `POST /api/productos`, haz clic en *Try it out*, modifica el JSON de ejemplo y ejecuta.

---

## Estructura del proyecto

```
tienda-demo-swagger/
├── docker-compose.yml
├── ms-productos/
│   ├── pom.xml                          ← dependencia springdoc agregada
│   └── src/main/java/com/duoc/productos/
│       ├── config/
│       │   └── SwaggerConfig.java       ← NUEVO: personaliza título y versión
│       ├── controller/
│       │   └── ProductoController.java  ← @Tag, @Operation, @ApiResponse, @Parameter
│       ├── dto/
│       │   ├── ProductoCreateDTO.java   ← @Schema en todos los campos
│       │   └── ProductoDTO.java
│       ├── model/Producto.java
│       ├── service/ProductoService.java
│       ├── repository/ProductoRepository.java
│       └── exception/...
│   └── src/main/resources/
│       └── application.properties      ← 3 propiedades springdoc.* agregadas
└── ms-pedidos/
    ├── pom.xml                          ← dependencia springdoc agregada
    └── src/main/java/com/duoc/pedidos/
        ├── config/
        │   └── SwaggerConfig.java       ← NUEVO
        ├── controller/
        │   └── PedidoController.java    ← @Tag, @Operation, @ApiResponse, @Parameter
        ├── dto/
        │   ├── PedidoCreateDTO.java     ← @Schema en todos los campos
        │   └── PedidoDTO.java
        ├── client/ProductoClient.java
        ├── model/Pedido.java
        ├── service/PedidoService.java
        └── exception/...
    └── src/main/resources/
        └── application.properties      ← 3 propiedades springdoc.* agregadas
```

---

## Resumen de todos los cambios respecto al proyecto base

| Archivo | Tipo de cambio | Qué se agregó |
|---|---|---|
| `ms-productos/pom.xml` | Modificado | Dependencia `springdoc-openapi-starter-webmvc-ui:2.6.0` |
| `ms-productos/application.properties` | Modificado | 3 propiedades `springdoc.*` |
| `ms-productos/config/SwaggerConfig.java` | **Nuevo** | Clase de configuración con título y descripción |
| `ms-productos/controller/ProductoController.java` | Modificado | `@Tag`, `@Operation`, `@ApiResponse`, `@Parameter` |
| `ms-productos/dto/ProductoCreateDTO.java` | Modificado | `@Schema` sobre la clase y cada campo |
| `ms-pedidos/pom.xml` | Modificado | Dependencia `springdoc-openapi-starter-webmvc-ui:2.6.0` |
| `ms-pedidos/application.properties` | Modificado | 3 propiedades `springdoc.*` |
| `ms-pedidos/config/SwaggerConfig.java` | **Nuevo** | Clase de configuración con título y descripción |
| `ms-pedidos/controller/PedidoController.java` | Modificado | `@Tag`, `@Operation`, `@ApiResponse`, `@Parameter` |
| `ms-pedidos/dto/PedidoCreateDTO.java` | Modificado | `@Schema` sobre la clase y cada campo |

---

DSY1103 Desarrollo Fullstack I · Unidad 3 · Duoc UC 2026-1
