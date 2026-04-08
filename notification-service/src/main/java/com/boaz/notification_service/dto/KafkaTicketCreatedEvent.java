package com.boaz.notification_service.dto;
public record KafkaTicketCreatedEvent(
		String eventType,
		String ticketId,
		String title,
		String priority,
		String createdByUserId,
		String assigneeId,
		String timestamp
) {}