package com.boaz.notification_service.dto;
import com.boaz.notification_service.enums.NotificationStatus;
import com.boaz.notification_service.enums.NotificationType;
import java.time.LocalDateTime;

public record NotificationResponse(
		String id,
		NotificationType type,
		NotificationStatus status,
		String recipient,
		String subject,
		String body,
		String referenceId,
		String sourceTopic,
		LocalDateTime createdAt
) {}