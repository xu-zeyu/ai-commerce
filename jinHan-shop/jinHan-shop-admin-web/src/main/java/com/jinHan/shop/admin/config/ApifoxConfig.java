package com.jinHan.shop.admin.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApifoxConfig {

    private static final String AUTHORIZATION_SECURITY_SCHEME = "Authorization";

    @Bean
    public OpenAPI adminOpenApi() {
        SecurityScheme bearerTokenScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .info(new Info()
                        .title("jinHan-shop 管理后台接口")
                        .version("0.0.1"))
                .components(new Components()
                        .addSecuritySchemes(AUTHORIZATION_SECURITY_SCHEME, bearerTokenScheme))
                .addSecurityItem(new SecurityRequirement()
                        .addList(AUTHORIZATION_SECURITY_SCHEME));
    }
}
