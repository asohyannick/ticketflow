package com.boaz.document_service.dto;
import java.time.LocalDateTime;

public record DocumentResponse(
		String id,
		String originalFileName,
		String contentType,
		Long fileSize,
		String ticketId,
		String uploadedByUserId,
		LocalDateTime uploadedAt
) {}