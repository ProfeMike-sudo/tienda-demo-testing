# Tienda Demo Testing — DSY1103

**DuocUC | Escuela de Informática y Telecomunicaciones**
**Asignatura:** Desarrollo Fullstack I — DSY1103 · Semestre 2026-1

---

> ### ⚠️ Fix aplicado — Error en dependencias de test (2026-06-16)
>
> **Problema:** Los tres starters de test definidos originalmente en ambos `pom.xml` no existen en Spring Boot:
> ```
> spring-boot-starter-data-jpa-test     ← no existe
> spring-boot-starter-validation-test   ← no existe
> spring-boot-starter-webmvc-test       ← no existe
> ```
> Maven no podía resolverlos, por lo que JUnit 5 y las anotaciones `@DataJpaTest`, `@WebMvcTest` nunca llegaban al classpath y los tests no corrían.
>
> **Solución:** Se reemplazaron los tres por el único starter oficial de test en Spring Boot:
> ```xml
> <dependency>
>     <groupId>org.springframework.boot</groupId>
>     <artifactId>spring-boot-starter-test</artifactId>
>     <scope>test</scope>
> </dependency>
> ```
> Este único starter trae todo lo necesario: JUnit 5, Mockito, AssertJ, `@DataJpaTest`, `@WebMvcTest` y `@SpringBootTest`.

---

## ¿Qué es este proyecto?

Repositorio de referencia para la **Unidad de Pruebas Unitarias** de DSY1103. Contiene una arquitectura de **microservicios Spring Boot** con:

- `ms-productos` — Microservicio catálogo de productos (puerto **8081**)
- `ms-pedidos` — Microservicio de órdenes (puerto **8082**)

El profesor tiene este proyecto desplegado en un servidor **AWS EC2** para que puedas explorar los endpoints con Swagger UI antes de implementar los tests en tu proyecto semestral.

---

## Stack y versiones

| Tecnología | Versión | Rol |
|------------|---------|-----|
| Java | 21 | Lenguaje principal |
| Spring Boot | **4.0.6** | Framework web |
| Spring Data JPA | 4.x | Acceso a datos |
| MySQL | 8.0 | Base de datos producción |
| H2 | 2.x | Base de datos en memoria (solo tests) |
| Docker / Docker Compose | Latest | Contenedores |
| JUnit 5 (Jupiter) | **6.0.3** | Framework de pruebas |
| Mockito | 5.x | Simulación de dependencias |
| springdoc-openapi | **3.0.3** | Swagger UI / OpenAPI 3 |

> **Compatibilidad importante:** Spring Boot 4.x requiere **springdoc-openapi 3.x** (no 2.x).
> Si ves el error `Failed to load API definition — response status is 500 /v3/api-docs`,
> revisa que tu `pom.xml` tenga `<version>3.0.3</version>` para `springdoc-openapi-starter-webmvc-ui`.

---

## Servidor AWS del Profesor (solo exploración)

| Recurso | URL |
|---------|-----|
| Swagger Productos | `http://52.23.53.73:8081/doc/swagger-ui.html` |
| Swagger Pedidos | `http://52.23.53.73:8082/doc/swagger-ui.html` |

> El servidor es solo para exploración y referencia. Los tests los ejecutas en tu máquina local.

---

## Datos de ejemplo en el servidor

El servidor ya tiene datos cargados para que pruebes los endpoints directamente en Swagger.

### Productos disponibles

| ID | Nombre | Precio | Stock |
|----|--------|--------|-------|
| 1 | Laptop Lenovo IdeaPad | $649.990 | 10 |
| 2 | Mouse Logitech MX Master 3 | $89.990 | 25 |
| 3 | Teclado Mecánico Redragon | $59.990 | 15 |
| 4 | Monitor Samsung 24" | $199.990 | 8 |
| 5 | Audífonos Sony WH-1000XM5 | $289.990 | 12 |
| 6 | Webcam Logitech C920 | $79.990 | 20 |

### Pedidos registrados

| ID | Producto | Cantidad | Total | Estado |
|----|----------|----------|-------|--------|
| 1 | Laptop Lenovo (ID 1) | 2 | $1.299.980 | PENDIENTE |
| 2 | Teclado Mecánico (ID 3) | 1 | $59.990 | CANCELADO |
| 3 | Mouse Logitech (ID 2) | 3 | $269.970 | PENDIENTE |

Puedes crear más datos usando **"Try it out"** en Swagger UI.

---

## Parte 1: Explorar el servidor del profesor

### Paso 1 — Abrir Swagger UI

- `http://52.23.53.73:8081/doc/swagger-ui.html` (Productos)
- `http://52.23.53.73:8082/doc/swagger-ui.html` (Pedidos)

### Paso 2 — Probar un endpoint

1. Clic en `GET /api/productos`
2. Clic en **"Try it out"** → **"Execute"**
3. Observa la respuesta JSON — esto es lo que tus tests verificarán

---

## Parte 2: Ejecutar el proyecto en tu máquina local

### Requisitos previos

```bash
java -version     # Java 17 o superior (recomendado 21)
mvn -version      # Maven 3.8+
docker --version  # Docker Desktop
git --version
```

### Paso 1 — Clonar el repositorio

```bash
git clone https://github.com/ProfeMike-sudo/tienda-demo-testing.git
cd tienda-demo-testing
```

### Paso 2 — Levantar todo con Docker Compose

```bash
docker compose up --build
```

Esto:
1. Crea 2 bases de datos MySQL (`productos_db` y `pedidos_db`)
2. Compila y levanta `ms-productos` en el puerto 8081
3. Compila y levanta `ms-pedidos` en el puerto 8082

> La primera vez puede tardar 10-15 minutos descargando imágenes y dependencias Maven.

### Paso 3 — Verificar que funciona

```bash
curl http://localhost:8081/api/productos
curl http://localhost:8082/api/pedidos
```

Swagger local:
- `http://localhost:8081/doc/swagger-ui.html`
- `http://localhost:8082/doc/swagger-ui.html`

---

## Parte 3: Ejecutar las Pruebas Unitarias

Los tests **no necesitan Docker ni MySQL** — usan una base de datos H2 en memoria.

### Ejecutar todos los tests de ms-productos

```bash
cd ms-productos
mvn test
```

### Resultado esperado

```
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Ejecutar un test específico

```bash
mvn test -Dtest=ProductoServiceTest#debeRetornarListaDeProductos
```

### Ver reporte HTML

```bash
# Mac/Linux:
open target/surefire-reports/index.html
# Windows:
start target/surefire-reports/index.html
```

### Estructura de tests incluidos

```
ms-productos/src/test/
├── java/com/duoc/productos/
│   ├── MsProductosApplicationTests.java     ← Test de contexto (carga el contexto Spring)
│   ├── service/
│   │   └── ProductoServiceTest.java          ← 6 tests de servicio con Mockito puro
│   └── controller/
│       └── ProductoControllerTest.java       ← 4 tests de controlador con MockMvc
└── resources/
    └── application.properties               ← Configuración H2 para tests
```

---

## Parte 4: Entender la Estructura de un Test

### Patrón Given-When-Then (AAA)

Todos los tests siguen este patrón obligatorio:

```java
@Test
@DisplayName("findAll - debe retornar lista de productos cuando existen registros")
void debeRetornarListaDeProductos() {

    // GIVEN (Arrange): prepara los datos y configura los mocks
    List<Producto> productosSimulados = List.of(
        new Producto(1L, "Laptop", "Laptop gaming", new BigDecimal("649990.00"), 10),
        new Producto(2L, "Mouse", "Mouse inalámbrico", new BigDecimal("89990.00"), 25)
    );
    when(repository.findAll()).thenReturn(productosSimulados);

    // WHEN (Act): ejecuta el método que estás probando
    List<ProductoDTO> resultado = productoService.findAll();

    // THEN (Assert): verifica que el resultado es el esperado
    assertNotNull(resultado);
    assertEquals(2, resultado.size());
    assertEquals("Laptop", resultado.get(0).getNombre());
    verify(repository, times(1)).findAll();
}
```

### Test de Servicio — Mockito puro (sin Spring)

```java
@ExtendWith(MockitoExtension.class)   // ← Solo Mockito, no carga contexto Spring
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;   // Simula el repositorio

    @InjectMocks
    private ProductoService productoService; // Inyecta el mock en el servicio real

    @Test
    @DisplayName("findById - debe lanzar excepción cuando el ID no existe")
    void debeLanzarExcepcionCuandoProductoNoExiste() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class, () ->
            productoService.findById(999L)
        );
    }
}
```

> **¿Por qué `@ExtendWith(MockitoExtension.class)` y no `@SpringBootTest`?**
> Con `@ExtendWith(MockitoExtension.class)` el test es **100% unitario** — no carga
> el contexto Spring, no necesita base de datos y termina en milisegundos.
> Úsalo siempre que puedas para tests de servicio.

### Test de Controlador con MockMvc

```java
@ExtendWith(MockitoExtension.class)   // ← Mockito puro, sin contexto Spring
class ProductoControllerTest {

    @Mock
    private ProductoService service;

    @InjectMocks
    private ProductoController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // standaloneSetup: configura MockMvc sin levantar el contexto Spring completo
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()) // carga el manejador de errores
                .build();
    }

    @Test
    @DisplayName("GET /api/productos - debe retornar 200 con lista de productos")
    void debeRetornar200CuandoSePidenProductos() throws Exception {
        // Given
        when(service.findAll()).thenReturn(List.of(
            new ProductoDTO(1L, "Laptop", "Laptop gaming", new BigDecimal("649990.00"), 10)
        ));

        // When & Then
        mockMvc.perform(get("/api/productos"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].nombre").value("Laptop"))
               .andExpect(jsonPath("$[0].precio").value(649990.00));
    }

    @Test
    @DisplayName("POST /api/productos - debe retornar 400 con datos inválidos")
    void debeRetornar400CuandoDatosInvalidos() throws Exception {
        // Given — nombre vacío y precio negativo
        String json = """
            { "nombre": "", "precio": -100, "stock": 5 }
            """;

        // When & Then
        mockMvc.perform(post("/api/productos")
               .contentType(MediaType.APPLICATION_JSON)
               .content(json))
               .andExpect(status().isBadRequest());
    }
}
```

---

## Parte 5: Resolución de Errores Comunes

### Error: `Failed to load API definition — 500 /v3/api-docs`

**Causa:** Versión de springdoc-openapi incompatible con Spring Boot 4.x.

**Solución:** En tu `pom.xml` usa la versión **3.0.3** (no 2.x):

```xml
<!-- CORRECTO para Spring Boot 4.x -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>3.0.3</version>
</dependency>

<!-- INCORRECTO — solo funciona con Spring Boot 3.x -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>  <!-- No usar con Spring Boot 4.x -->
</dependency>
```

---

### Error: `Cannot load driver class: com.mysql.cj.jdbc.Driver` al ejecutar tests

**Causa:** Los tests intentan conectarse a MySQL real, que no está corriendo.

**Solución:** Agrega H2 a tu `pom.xml` en scope test y crea `src/test/resources/application.properties`:

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

```properties
# src/test/resources/application.properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

---

### Error: `Cannot resolve symbol 'MockBean'` en Spring Boot 4.x

**Causa:** En Spring Boot 4.x, `@MockBean` fue reemplazado por `@MockitoBean`.

**Solución:**

```java
// Spring Boot 3.x (deprecated en 4.x)
import org.springframework.boot.test.mock.mockito.MockBean;
@MockBean
private ProductoService service;

// Spring Boot 4.x (correcto)
import org.springframework.test.context.bean.override.mockito.MockitoBean;
@MockitoBean
private ProductoService service;
```

---

### Error: `docker compose up` se queda colgado o falla con `OOMKilled`

**Causa:** Falta de RAM. El build de dos proyectos Maven simultáneamente requiere al menos 4 GB.

**Solución:** Aumenta la memoria de Docker Desktop en Settings → Resources → Memory (mínimo 4 GB recomendado).

---

### Error: `Port 8081 is already in use`

**Solución:**

```bash
# Detener contenedores previos
docker compose down

# O encontrar y matar el proceso que usa el puerto
# Mac/Linux:
lsof -i :8081 | awk 'NR>1 {print $2}' | xargs kill -9
# Windows (PowerShell):
netstat -ano | findstr :8081
taskkill /PID <número_pid> /F
```

---

## Parte 6: Aplica esto en tu Proyecto Semestral

### Checklist de implementación

- [ ] `spring-boot-starter-test` en tu `pom.xml`
- [ ] H2 en scope test + `application.properties` en test/resources
- [ ] **2 tests de servicio**: con `@ExtendWith(MockitoExtension.class)` y Mockito
- [ ] **1 test de controlador**: con `@WebMvcTest` y `@MockitoBean`
- [ ] Patrón Given / When / Then en todos los tests
- [ ] `@DisplayName` descriptivo en cada test
- [ ] `mvn test` pasa sin errores
- [ ] `TESTING_PLAN.md` documentando tus reglas de negocio

### Plantilla TESTING_PLAN.md

```markdown
## Plan de Pruebas Unitarias — [Nombre de tu proyecto]

### Reglas de Negocio Críticas

1. **[Regla 1]**: Descripción de la regla.
2. **[Regla 2]**: Descripción de la regla.

### Cobertura Actual

| Regla | Estado | Casos Cubiertos | Pendiente |
|-------|--------|-----------------|-----------|
| 1. [Nombre] | ✅ Cubierta | Caso feliz, caso error | — |
| 2. [Nombre] | ⚠️ Parcial | Solo caso feliz | Caso error |

### Reflexión y Deuda Técnica

- **Riesgo identificado**: [Qué regla no está probada y qué podría fallar]
- **Acción fuerta**: [Qué test agregarías con más tiempo]
```

---

## Estructura del Proyecto

```
tienda-demo-testing/
├── docker-compose.yml
├── ms-productos/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/duoc/productos/
│       │   ├── controller/ProductoController.java
│       │   ├── service/ProductoService.java
│       │   ├── repository/ProductoRepository.java
│       │   ├── model/Producto.java
│       │   ├── dto/ProductoDTO.java
│       │   ├── dto/ProductoCreateDTO.java
│       │   ├── exception/RecursoNoEncontradoException.java
│       │   ├── exception/GlobalExceptionHandler.java
│       │   └── config/SwaggerConfig.java
│       └── test/java/com/duoc/productos/
│           ├── MsProductosApplicationTests.java   ← Contexto Spring + H2
│           ├── service/
│           │   └── ProductoServiceTest.java        ← 6 tests unitarios
│           └── controller/
│               └── ProductoControllerTest.java     ← 4 tests de controlador
├── ms-pedidos/
│   ├── pom.xml
│   └── src/...
└── README.md
```

---

## Información del Curso

| Campo | Detalle |
|-------|---------|
| **Asignatura** | Desarrollo Fullstack I |
| **Código** | DSY1103 |
| **Unidad** | Pruebas Unitarias con JUnit 5 y Mockito |
| **Institución** | DuocUC — Escuela de Informática y Telecomunicaciones |
| **Semestre** | 2026-1 |
| **Profesor** | Michael Catalán |
| **Servidor demo** | `http://52.23.53.73:8081` / `http://52.23.53.73:8082` |

---

*DuocUC — Cercanía. Liderazgo. Futuro.*
