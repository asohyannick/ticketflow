package com.boaz.api_gateway.config.swaggerConfig;

import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class SwaggerConfig {

		@Bean
		@Primary
		public SwaggerUiConfigProperties swaggerUiConfigProperties() {
			SwaggerUiConfigProperties config = new SwaggerUiConfigProperties();
			
			Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls =
					new HashSet<>();
			
			urls.add(swaggerUrl(
					"user-service",
					"/user-service/v3/api-docs"
			));
			urls.add(swaggerUrl(
					"ticket-service",
					"/ticket-service/v3/api-docs"
			));
			urls.add(swaggerUrl(
					"notification-service",
					"/notification-service/v3/api-docs"
			));
			urls.add(swaggerUrl(
					"document-service",
					"/document-service/v3/api-docs"
			));
			
			config.setUrls(urls);
			return config;
		}
		
		private AbstractSwaggerUiConfigProperties.SwaggerUrl swaggerUrl(
				String name, String url) {
			AbstractSwaggerUiConfigProperties.SwaggerUrl swaggerUrl =
					new AbstractSwaggerUiConfigProperties.SwaggerUrl();
			swaggerUrl.setName(name);
			swaggerUrl.setUrl(url);
			return swaggerUrl;
		}
}