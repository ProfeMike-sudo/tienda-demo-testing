# 🛒 Tienda Demo Testing — DSY1103

**DuocUC | Escuela de Informática y Telecomunicaciones**  
**Asignatura:** Desarrollo Fullstack I — DSY1103 · Semestre 2026-1

---

## 📋 ¿Qué es este proyecto?

Este repositorio es el proyecto de referencia para la **Unidad de Pruebas Unitarias** de DSY1103. Contiene una arquitectura de **microservicios Spring Boot** (Tienda Demo) con:

- `ms-productos` — Microservicio catálogo de productos (puerto **8081**)
- `ms-pedidos` — Microservicio de órdenes (puerto **8082**)

El profesor ha desplegado este proyecto en un servidor **AWS EC2** para que puedas explorarlo con Swagger UI antes de implementar los tests en tu propio proyecto semestral.

---

## 🌐 Servidor AWS del Profesor (solo exploración)

| Recurso | URL |
|---------|-----|
| 📦 Swagger Productos | `http://52.23.53.73:8081/doc/swagger-ui.html` |
| 🛍️ Swagger Pedidos | `http://52.23.53.73:8082/doc/swagger-ui.html` |

> **Importante:** El servidor es solo para exploración y referencia. Los tests debes ejecutarlos en tu máquina local.

---

## 🛠️ Tecnologías del Proyecto

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 17+ | Lenguaje principal |
| Spring Boot | 3.x | Framework web |
| Spring Data JPA | 3.x | Acceso a datos |
| MySQL | 8.0 | Base de datos |
| Docker / Docker Compose | Latest | Contenedores |
| JUnit 5 | 5.x | Framework de pruebas |
| Mockito | 5.x | Simulación de dependencias |
| Swagger / OpenAPI 3 | 2.6.0 | Documentación de la API |

---

## 🚀 Parte 1: Explorar el servidor del profesor

### Paso 1 — Acceder a Swagger UI

Abre cualquiera de estas URLs en tu navegador:

- `http://52.23.53.73:8081/doc/swagger-ui.html` (Productos)
- `http://52.23.53.73:8082/doc/swagger-ui.html` (Pedidos)

### Paso 2 — Explorar los endpoints

Verás una lista de todos los endpoints agrupados por controlador. Por ejemplo:

```
GET  /api/productos        → Listar todos los productos
POST /api/productos        → Crear un producto
GET  /api/productos/{id}   → Buscar producto por ID
PUT  /api/productos/{id}   → Actualizar producto
DELETE /api/productos/{id} → Eliminar producto
```

### Paso 3 — Probar un endpoint

1. Haz clic en `GET /api/productos`
2. Haz clic en **"Try it out"**
3. Presiona **"Execute"**
4. Observa la respuesta JSON — esto es lo que tus tests deben verificar

---

## 💻 Parte 2: Ejecutar el proyecto en tu máquina local

### Requisitos previos

Instala y verifica:

```bash
# Java 17 o superior
java -version

# Maven 3.8+
mvn -version

# Docker Desktop (para levantar las bases de datos)
docker --version
docker compose version

# Git
git --version
```

> **¿No tienes Docker?** Descárgalo en [docker.com/get-started](https://www.docker.com/get-started)

### Paso 1 — Clonar el repositorio

```bash
git clone https://github.com/ProfeMike-sudo/tienda-demo-testing.git
cd tienda-demo-testing
```

### Paso 2 — Levantar todo con Docker Compose

```bash
docker compose up --build
```

Este comando:
1. Crea 2 bases de datos MySQL (`productos_db` y `pedidos_db`)
2. Compila y levanta `ms-productos` en el puerto 8081
3. Compila y levanta `ms-pedidos` en el puerto 8082

> La primera vez puede tardar 5-10 minutos descargando imágenes.

### Paso 3 — Verificar que funciona

```bash
# Ver que los 4 contenedores estén corriendo
docker ps

# Probar ms-productos
curl http://localhost:8081/api/productos

# Probar ms-pedidos
curl http://localhost:8082/api/pedidos
```

Abre en el navegador:
- `http://localhost:8081/doc/swagger-ui.html` (Productos local)
- `http://localhost:8082/doc/swagger-ui.html` (Pedidos local)

---

## 🧪 Parte 3: Ejecutar las Pruebas Unitarias

### Paso 1 — Abrir un microservicio en tu IDE

```bash
# Abrir ms-productos en VS Code
code ms-productos/

# O en IntelliJ IDEA: File → Open → selecciona ms-productos/
```

### Paso 2 — Verificar las dependencias de testing en pom.xml

Tu `pom.xml` debe tener (ya viene configurado en este proyecto):

```xml
<!-- JUnit 5 + Spring Test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito (incluido en spring-boot-starter-test, pero explícito para claridad) -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
```

### Paso 3 — Estructura de los tests

```
ms-productos/
└── src/
    └── test/
        └── java/
            └── com/duoc/productos/
                ├── MsProductosApplicationTests.java  ← Test de contexto
                ├── service/
                │   └── ProductoServiceTest.java      ← Tests de servicio
                └── controller/
                    └── ProductoControllerTest.java   ← Tests de controlador
```

### Paso 4 — Ejecutar los tests

**Desde la terminal:**
```bash
cd ms-productos
mvn test
```

**Desde el IDE:**
- IntelliJ: clic derecho en la carpeta `test` → **Run All Tests**
- VS Code: clic en el ícono ▶️ junto a cada método `@Test`

**Ver reporte HTML:**
```bash
# Después de mvn test
# En Mac/Linux:
open ms-productos/target/surefire-reports/index.html
# En Windows:
start ms-productos/target/surefire-reports/index.html
```

---

## 📝 Parte 4: Entender la Estructura de un Test

### Patrón Given-When-Then (AAA)

Todos los tests siguen este patrón obligatorio:

```java
@Test
@DisplayName("Debe retornar lista de productos cuando existen")
void debeRetornarListaDeProductos() {
    
    // ✅ GIVEN (Arrange): prepara los datos y configura los mocks
    List<Producto> productosSimulados = List.of(
        new Producto(1L, "Laptop", 999990.0, 10),
        new Producto(2L, "Mouse", 25990.0, 50)
    );
    when(productoRepository.findAll()).thenReturn(productosSimulados);

    // ✅ WHEN (Act): ejecuta el método que estás probando
    List<ProductoDTO> resultado = productoService.findAll();

    // ✅ THEN (Assert): verifica que el resultado es el esperado
    assertNotNull(resultado);
    assertEquals(2, resultado.size());
    assertEquals("Laptop", resultado.get(0).getNombre());
}
```

### Test de Servicio (el más común)

```java
@SpringBootTest
class ProductoServiceTest {

    // 🔧 Simula el repositorio (no toca la BD real)
    @MockBean
    private ProductoRepository productoRepository;

    // ✅ Inyecta el servicio real que queremos probar
    @Autowired
    private ProductoService productoService;

    @Test
    @DisplayName("Debe retornar lista vacía si no hay productos")
    void debeRetornarListaVaciaSiNoHayProductos() {
        // Given
        when(productoRepository.findAll()).thenReturn(List.of());

        // When
        List<ProductoDTO> resultado = productoService.findAll();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Debe lanzar excepción si producto no existe")
    void debeLanzarExcepcionSiProductoNoExiste() {
        // Given
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            productoService.findById(999L);
        });
    }
}
```

### Test de Controlador con MockMvc

```java
@SpringBootTest
@AutoConfigureMockMvc
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // 🔧 Simula el servicio para que el controller no dependa de la BD
    @MockBean
    private ProductoService productoService;

    @Test
    @DisplayName("GET /api/productos debe retornar 200 OK con lista")
    void debeRetornar200CuandoSePidenProductos() throws Exception {
        // Given
        when(productoService.findAll()).thenReturn(
            List.of(new ProductoDTO(1L, "Laptop", 999990.0))
        );

        // When & Then (en un solo paso con MockMvc)
        mockMvc.perform(get("/api/productos"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].nombre").value("Laptop"))
               .andExpect(jsonPath("$[0].precio").value(999990.0));
    }

    @Test
    @DisplayName("POST /api/productos debe retornar 201 Created")
    void debeRetornar201AlCrearProducto() throws Exception {
        // Given
        String jsonProducto = """
            {
                "nombre": "Teclado Mecánico",
                "precio": 79990.0,
                "stock": 15
            }
            """;

        when(productoService.crear(any())).thenReturn(
            new ProductoDTO(3L, "Teclado Mecánico", 79990.0)
        );

        // When & Then
        mockMvc.perform(post("/api/productos")
               .contentType(MediaType.APPLICATION_JSON)
               .content(jsonProducto))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.nombre").value("Teclado Mecánico"));
    }
}
```

---

## ✅ Parte 5: Aplica esto en tu Proyecto Semestral

Después de entender y ejecutar los tests de este proyecto, replica el patrón en tu proyecto:

### Checklist de implementación

- [ ] **Dependencias**: Agregar `spring-boot-starter-test` en tu `pom.xml`
- [ ] **2 tests de servicio**: Implementar con `@MockBean` y Mockito
- [ ] **1 test de controlador**: Implementar con `MockMvc`
- [ ] **Patrón AAA**: Todos los tests deben tener Given / When / Then
- [ ] **Nombres descriptivos**: Usar `@DisplayName` en cada test
- [ ] **Tests pasan**: `mvn test` debe terminar sin errores
- [ ] **TESTING_PLAN.md**: Documentar tus reglas de negocio y cobertura

### Plantilla TESTING_PLAN.md para tu proyecto

```markdown
## Plan de Pruebas Unitarias — [Nombre de tu proyecto]

### Reglas de Negocio Críticas

1. **[Regla 1]**: Descripción de la regla.
2. **[Regla 2]**: Descripción de la regla.
3. **[Regla 3]**: Descripción de la regla.

### Cobertura Actual

| Regla | Estado | Casos Cubiertos | Pendiente |
|-------|--------|-----------------|-----------|
| 1. [Nombre] | ✅ Cubierta | Caso feliz, caso error | — |
| 2. [Nombre] | ⚠️ Parcial | Solo caso feliz | Caso error |
| 3. [Nombre] | ❌ Pendiente | — | Todo |

### Reflexión y Deuda Técnica

- **Riesgo identificado**: [Describe qué regla no está probada y qué podría fallar]
- **Acción futura**: [Qué test agregarías si tuvieras más tiempo]
```

---

## ❓ Preguntas Frecuentes

**¿Por qué uso `@MockBean` en vez de conectarme a la BD real?**
> Las pruebas unitarias deben ser rápidas y aisladas. Conectarse a una BD real las hace lentas y dependientes del estado de los datos. `@MockBean` simula el repositorio de forma controlada y predecible.

**¿Qué diferencia hay entre `@Mock` y `@MockBean`?**
> - `@Mock` es Mockito puro (para clases sin contexto Spring)
> - `@MockBean` reemplaza el bean real en el contexto Spring Boot (para tests con `@SpringBootTest`)

**¿Puedo ejecutar solo un test?**
> Sí:
> ```bash
> mvn test -Dtest=ProductoServiceTest#debeRetornarListaDeProductos
> ```

**El `docker compose up` falla ¿qué hago?**
> Verifica que Docker Desktop esté corriendo: busca el ícono de Docker en la barra de tareas. Si no está, ábrelo primero.

**¿Cómo paro los contenedores cuando termino?**
> ```bash
> docker compose down
> # Para eliminar también los datos de BD:
> docker compose down -v
> ```

---

## 📁 Estructura del Proyecto

```
tienda-demo-testing/
├── docker-compose.yml              ← Levanta todo con un comando
├── ms-productos/                   ← Microservicio de productos
│   ├── pom.xml                     ← Dependencias Maven
│   └── src/
│       ├── main/java/com/duoc/productos/
│       │   ├── controller/         ← Endpoints REST
│       │   ├── service/            ← Lógica de negocio
│       │   ├── repository/         ← Acceso a datos JPA
│       │   ├── model/              ← Entidades JPA
│       │   ├── dto/                ← DTOs de entrada/salida
│       │   └── config/SwaggerConfig.java
│       └── test/java/com/duoc/productos/
│           └── MsProductosApplicationTests.java
├── ms-pedidos/                     ← Microservicio de pedidos
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/duoc/pedidos/
│       └── test/java/com/duoc/pedidos/
│           └── MsPedidosApplicationTests.java
└── README.md                       ← Esta guía
```

---

## 👨‍🏫 Información del Curso

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
