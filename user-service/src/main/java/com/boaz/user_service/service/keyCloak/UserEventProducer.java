package com.boaz.user_service.service.keyCloak;
import com.boaz.user_service.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	
	@Value("${kafka.producer.topic.user-created:user.created}")
	private String userCreatedTopic;
	
	public void publishUserCreated( User user) {
		Map<String, Object> event = new HashMap<>();
		event.put("eventType", "USER_CREATED");
		event.put("userId", user.getId());
		event.put("email", user.getEmail());
		event.put("firstName", user.getFirstName());
		event.put("lastName", user.getLastName());
		event.put("timestamp", LocalDateTime.now().toString());
		
		kafkaTemplate.send(userCreatedTopic, user.getId(), event);
		log.info("Published USER_CREATED event for user: {}", user.getEmail());
	}
}