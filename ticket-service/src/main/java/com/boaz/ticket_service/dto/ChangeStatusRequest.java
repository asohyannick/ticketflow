package com.boaz.ticket_service.dto;
import com.boaz.ticket_service.constant.TicketStatus;
import jakarta.validation.constraints.NotNull;
public record ChangeStatusRequest(
		@NotNull (message = "Status is required")
		TicketStatus status
) { }
