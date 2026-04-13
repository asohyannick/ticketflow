package com.boaz.document_service.config.openApiConfig;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

@Bean
public OpenAPI customOpenAPI() {
	return new OpenAPI()
			       .info(new Info()
					             .title("Support Ticket Management System")
					             .description("Document Service API")
					             .version("1.0.0")
					             .contact(new Contact()
							                      .name("TicketFlow Team")
							                      .email("support@ticketflow.com"))
					             .license(new License()
							                      .name("Apache 2.0")
							                      .url("https://www.apache.org/licenses/LICENSE-2.0")));
}
}