package com.boaz.ticket_service.dto;
import jakarta.validation.constraints.NotBlank;
public record AddCommentRequest(
		@NotBlank (message = "Comment content is required")
		String content
) { }
