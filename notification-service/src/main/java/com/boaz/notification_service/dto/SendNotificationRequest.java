package com.boaz.notification_service.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public record SendNotificationRequest(
		
		@NotBlank(message = "Recipient email is required")
		@Email(message = "Must be a valid email address")
		String recipient,
		
		@NotBlank(message = "Subject is required")
		String subject,
		
		@NotBlank(message = "Body is required")
		String body
) {}