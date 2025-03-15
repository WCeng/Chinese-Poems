package cn.wceng.poem;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .openapi("3.0.1") // 明确指定 OpenAPI 版本
            .info(new Info()
                .title("Poem API")
                .version("1.0")
                .description("API documentation for Poem Management System"));
    }
}