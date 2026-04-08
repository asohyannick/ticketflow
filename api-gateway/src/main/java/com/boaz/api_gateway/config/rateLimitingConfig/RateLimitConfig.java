package com.boaz.api_gateway.config.rateLimitingConfig;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

		@Bean
		public RedisRateLimiter redisRateLimiter() {
			return new RedisRateLimiter(
					20,
					40,
					1
			);
		}
		
		@Bean
		public KeyResolver userKeyResolver() {
			return exchange -> exchange.getPrincipal()
					                   .map(principal -> principal.getName())
					                   .defaultIfEmpty(
							                   exchange.getRequest()
									                   .getRemoteAddress()
									                   .getAddress()
									                   .getHostAddress()
					                   );
		}
}