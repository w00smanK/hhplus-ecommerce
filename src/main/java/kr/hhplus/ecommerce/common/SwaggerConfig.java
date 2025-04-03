package kr.hhplus.ecommerce.common;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info()
                .title("e-commerce API")
                .description("e-commerce API Swagger Documentation")
                .version("1.0.0");

        Server localServer = new Server()
                .description("local")
                .url("http://localhost:8080");

        return new OpenAPI()
                .components(new Components())
                .servers(Collections.singletonList(localServer))
                .tags(Collections.emptyList())
                .info(info);
    }
}
