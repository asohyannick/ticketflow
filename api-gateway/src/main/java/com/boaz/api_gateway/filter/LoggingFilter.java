package com.boaz.api_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {
		
		@Override
		public int getOrder() {
			return Ordered.HIGHEST_PRECEDENCE;
		}
		
		@Override
		public Mono<Void> filter(
				ServerWebExchange exchange,
				GatewayFilterChain chain
		) {
			
			ServerHttpRequest request = exchange.getRequest();
			long startTime = Instant.now().toEpochMilli();
			
			log.info("[GATEWAY] --> {} {} | Headers: Authorization={}",
					request.getMethod(),
					request.getURI().getPath(),
					request.getHeaders().containsKey("Authorization")
							? "present" : "absent"
			);
			
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				ServerHttpResponse response = exchange.getResponse();
				long duration = Instant.now().toEpochMilli() - startTime;
				
				log.info("[GATEWAY] <-- {} {} | Status: {} | {}ms",
						request.getMethod(),
						request.getURI().getPath(),
						response.getStatusCode(),
						duration
				);
			}));
		}
}