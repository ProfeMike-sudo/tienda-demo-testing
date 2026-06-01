package com.duoc.pedidos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Pedidos Service")
                        .version("1.0")
                        .description("Microservicio de gestión de pedidos de la tienda. "
                                + "Permite crear pedidos, consultarlos y cancelarlos. "
                                + "Se comunica con ms-productos para validar el catálogo."));
    }
}
