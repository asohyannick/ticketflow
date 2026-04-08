package com.boaz.notification_service.dto;

public record KafkaUserCreatedEvent(
		String eventType,
		String userId,
		String email,
		String firstName,
		String lastName,
		String timestamp
) {}