package com.boaz.ticket_service.dto;
import java.time.LocalDateTime;
public record CommentResponse(
		String id,
		String content,
		String authorId,
		LocalDateTime createdAt
) {
}
