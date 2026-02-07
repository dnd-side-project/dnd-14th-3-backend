package com.dnd.jjigeojulge.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(info = @Info(title = "찍어줄게 SWAGGER", description = "\uD83D\uDCF8 찍어줄게 API 명세서", version = "v1"))
public class OpenAPIConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI().components(new Components().addSecuritySchemes("bearer-key",
			new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
	}
}
