package com.boaz.user_service.config.kafkaConfig;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class KeyCloakConfig {

	@Value("${keycloak.server-url}")
	private String serverUrl;
	
	@Value("${keycloak.realm}")
	private String realm;
	
	@Value("${keycloak.client-id}")
	private String clientId;
	
	@Value("${keycloak.client-secret}")
	private String clientSecret;
	
	@Value("${keycloak.admin-username}")
	private String adminUsername;
	
	@Value("${keycloak.admin-password}")
	private String adminPassword;
	
	@Bean
	public Keycloak keycloak() {
		return KeycloakBuilder.builder()
				       .serverUrl(serverUrl)
				       .realm("master")
				       .clientId("admin-cli")
				       .username(adminUsername)
				       .password(adminPassword)
				       .build();
}
}