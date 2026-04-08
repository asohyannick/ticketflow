package com.boaz.user_service.config.securityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
		
		@Bean
		public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
			http
					.csrf(csrf -> csrf.disable())
					.sessionManagement(session ->
							                   session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.authorizeHttpRequests(auth -> auth
							                               // Swagger and actuator are public
							                               .requestMatchers(
									                               "/swagger-ui/**",
									                               "/v3/api-docs/**",
									                               "/actuator/health",
									                               "/actuator/prometheus"
							                               ).permitAll()
							                               .anyRequest().authenticated()
					)
					.oauth2ResourceServer(oauth2 -> oauth2
							                                .jwt(jwt -> jwt
									                                            .jwtAuthenticationConverter(jwtAuthenticationConverter())
							                                )
					);
			
			return http.build();
		}
		
		@Bean
		public JwtAuthenticationConverter jwtAuthenticationConverter() {
			JwtGrantedAuthoritiesConverter authoritiesConverter =
					new JwtGrantedAuthoritiesConverter();
			
			authoritiesConverter.setAuthoritiesClaimName("scope");
			
			authoritiesConverter.setAuthorityPrefix("");
			
			JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
			converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
			return converter;
		}
}