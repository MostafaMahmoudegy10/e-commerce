package org.stylehub.backend.e_commerce.platform.config.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class OpenApiConfiguration {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI styleHubOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("StyleHub API")
                        .description("AI-driven fashion social commerce platform combining social media, multi-vendor e-commerce, recommendation system, and brand-model collaboration.")
                        .version("v1.0"))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }

    @Bean
    public OpenApiCustomizer publicEndpointSecurityCustomizer() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }

            openApi.getPaths().forEach((path, pathItem) -> {
                if (path.equals("/api/v1/public") || path.startsWith("/api/v1/public/")) {
                    pathItem.readOperations().forEach(operation -> operation.setSecurity(Collections.emptyList()));
                }
            });
        };
    }

    @Bean
    public GroupedOpenApi publicApis() {
        return GroupedOpenApi.builder()
                .group("public-apis")
                .displayName("Public APIs")
                .pathsToMatch("/api/v1/public/**", "api/v1/public/**")
                .build();
    }

    @Bean
    public GroupedOpenApi customerApis() {
        return GroupedOpenApi.builder()
                .group("customer-apis")
                .displayName("Customer APIs")
                .pathsToMatch("/api/v1/customer/**", "api/v1/customer/**")
                .build();
    }

    @Bean
    public GroupedOpenApi brandDashboardApis() {
        return GroupedOpenApi.builder()
                .group("brand-dashboard-apis")
                .displayName("Brand Dashboard APIs")
                .pathsToMatch("/api/v1/brands/**", "api/v1/brands/**")
                .build();
    }

    @Bean
    public GroupedOpenApi modelApis() {
        return GroupedOpenApi.builder()
                .group("model-apis")
                .displayName("Model APIs")
                .pathsToMatch("/api/v1/model/**", "api/v1/model/**", "/api/v1/models/**", "api/v1/models/**")
                .build();
    }

    @Bean
    public GroupedOpenApi ordersAndPaymentsApis() {
        return GroupedOpenApi.builder()
                .group("orders-payments-apis")
                .displayName("Orders & Payments APIs")
                .pathsToMatch(
                        "/api/v1/orders/**",
                        "api/v1/orders/**",
                        "/api/v1/payments/**",
                        "api/v1/payments/**",
                        "/api/v1/customer/payments/**",
                        "api/v1/customer/payments/**",
                        "/api/v1/customer/**/checkout",
                        "api/v1/customer/**/checkout",
                        "/api/v1/customer/**/checkout/**",
                        "api/v1/customer/**/checkout/**",
                        "/api/v1/brands/orders/**",
                        "api/v1/brands/orders/**",
                        "/api/v1/brands/model-agreements/**/payments/**",
                        "api/v1/brands/model-agreements/**/payments/**")
                .build();
    }
}
