package com.boaz.ticket_service.dto;

import com.boaz.ticket_service.constant.TicketPriority;

public record CreateTicketRequest(
		String title,
		String description,
		TicketPriority priority,
		String assigneeId
) {
}
