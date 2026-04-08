package com.boaz.ticket_service.feign;
import com.boaz.ticket_service.dto.UserDto;
import com.boaz.ticket_service.resilience.UserServiceFallback;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
		name = "user-service",
		fallback = UserServiceFallback.class
)
public interface UserServiceClient {
	
	@GetMapping("/api/users/{id}")
	@CircuitBreaker(name = "user-service", fallbackMethod = "getUserFallback")
	UserDto getUserById( @PathVariable("id") String userId);
}