package com.boaz.ticket_service.resilience;
import com.boaz.ticket_service.dto.UserDto;
import com.boaz.ticket_service.feign.UserServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Component
@Slf4j
public class UserServiceFallback implements UserServiceClient {

@Override
public UserDto getUserById(String userId) {
	log.warn("FALLBACK: user-service is unavailable for userId={}. Returning degraded response.", userId);
	
	return new UserDto(
			userId,
			"unavailable@ticketflow.com",
			"Service",
			"Unavailable",
			false
	);
}
}