package com.boaz.document_service.dto;
import java.time.LocalDateTime;
public record PresignedUrlResponse(
		String documentId,
		String downloadUrl,
		LocalDateTime expiresAt,
		int expiryMinutes
) {}