package com.boaz.document_service.dto;
import java.time.LocalDateTime;
public record DocumentEventPayload(
		String eventType,
		String documentId,
		String originalFileName,
		String contentType,
		Long fileSize,
		String ticketId,
		String uploadedByUserId,
		LocalDateTime timestamp
) {}