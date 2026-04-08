package com.boaz.document_service.dto;
import java.time.LocalDateTime;
public record UploadResponse(
		String id,
		String originalFileName,
		String contentType,
		Long fileSize,
		String ticketId,
		LocalDateTime uploadedAt,
		String message
) {}