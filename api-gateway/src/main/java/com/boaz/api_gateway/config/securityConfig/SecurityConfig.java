package com.boaz.api_gateway.config.securityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

		@Bean
		public SecurityWebFilterChain springSecurityFilterChain(
				ServerHttpSecurity http) {
			
			http
					.csrf(ServerHttpSecurity.CsrfSpec::disable)
					
					.cors(cors -> cors.configurationSource(corsConfigurationSource()))
					
					.authorizeExchange(exchanges -> exchanges
							                                
							                                // ── Public endpoints — no JWT required ──
							                                .pathMatchers(
									                                "/actuator/health",
									                                "/actuator/prometheus",
									                                "/swagger-ui.html",
									                                "/swagger-ui/**",
									                                "/v3/api-docs/**",
									                                "/webjars/**",
									                                // Per-service swagger docs (aggregated by gateway)
									                                "/user-service/v3/api-docs",
									                                "/ticket-service/v3/api-docs",
									                                "/notification-service/v3/api-docs",
									                                "/document-service/v3/api-docs"
							                                ).permitAll()
							                                
							                                .anyExchange().authenticated()
					)
					
					.oauth2ResourceServer(oauth2 -> oauth2
							                                .jwt(jwt -> jwt
									                                            .jwtAuthenticationConverter(
											                                            jwtAuthenticationConverter())
							                                )
					);
			
			return http.build();
		}
		
		@Bean
		public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
			JwtGrantedAuthoritiesConverter authoritiesConverter =
					new JwtGrantedAuthoritiesConverter();
			
			authoritiesConverter.setAuthoritiesClaimName("scope");
			
			authoritiesConverter.setAuthorityPrefix("");
			
			JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
			jwtConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
			
			return new ReactiveJwtAuthenticationConverterAdapter(jwtConverter);
		}
		
		@Bean
		public CorsConfigurationSource corsConfigurationSource() {
			CorsConfiguration config = new CorsConfiguration();
			
			config.setAllowedOriginPatterns(List.of(
					"http://localhost:3000",
					"http://localhost:4200",
					"http://localhost:5173",
					"http://localhost:8080"     // same-origin gateway calls
			));
			
			config.setAllowedMethods(List.of(
					"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
			));
			
			config.setAllowedHeaders(List.of(
					"Authorization",
					"Content-Type",
					"Accept",
					"Origin",
					"X-Requested-With"
			));
			
			config.setExposedHeaders(List.of(
					"Authorization",
					"Content-Disposition"
			));
			
			config.setAllowCredentials(true);
			
			config.setMaxAge(3600L);
			
			UrlBasedCorsConfigurationSource source =
					new UrlBasedCorsConfigurationSource();
			
			source.registerCorsConfiguration("/**", config);
			return source;
		}
}