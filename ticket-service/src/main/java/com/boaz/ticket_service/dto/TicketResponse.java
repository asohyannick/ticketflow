package com.boaz.ticket_service.dto;
import com.boaz.ticket_service.constant.TicketPriority;
import com.boaz.ticket_service.constant.TicketStatus;
import java.time.LocalDateTime;
import java.util.List;
public record TicketResponse(
		String id,
		String title,
		String description,
		TicketStatus status,
		TicketPriority priority,
		String assigneeId,
		String createdByUserId,
		LocalDateTime createdAt,
		LocalDateTime updatedAt,
		LocalDateTime resolvedAt,
		List<CommentResponse> comments,
		List <String> documentIds
) {
}
