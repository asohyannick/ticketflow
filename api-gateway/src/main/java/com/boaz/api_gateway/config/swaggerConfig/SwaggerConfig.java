package com.boaz.api_gateway.config.swaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
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
public OpenAPI customOpenAPI() {
	return new OpenAPI()
			       .info(new Info()
					             .title("Support Ticket Management System")
					             .description("Centralized API documentation for all TicketFlow microservices")
					             .version("1.0.0")
					             .contact(new Contact()
							                      .name("TicketFlow Team")
							                      .email("support@ticketflow.com"))
					             .license(new License()
							                      .name("Apache 2.0")
							                      .url("https://www.apache.org/licenses/LICENSE-2.0")));
}

@Bean
@Primary
public SwaggerUiConfigProperties swaggerUiConfigProperties() {
	SwaggerUiConfigProperties config = new SwaggerUiConfigProperties();
	
	Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = new HashSet<>();
	
	urls.add(swaggerUrl("user-service", "/user-service/v3/api-docs"));
	urls.add(swaggerUrl("ticket-service", "/ticket-service/v3/api-docs"));
	urls.add(swaggerUrl("notification-service", "/notification-service/v3/api-docs"));
	urls.add(swaggerUrl("document-service", "/document-service/v3/api-docs"));
	
	config.setUrls(urls);
	return config;
}

private AbstractSwaggerUiConfigProperties.SwaggerUrl swaggerUrl(String name, String url) {
	AbstractSwaggerUiConfigProperties.SwaggerUrl swaggerUrl =
			new AbstractSwaggerUiConfigProperties.SwaggerUrl();
	swaggerUrl.setName(name);
	swaggerUrl.setUrl(url);
	return swaggerUrl;
}
}