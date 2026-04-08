package com.boaz.ticket_service.controller;
import com.boaz.ticket_service.dto.*;
import com.boaz.ticket_service.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Ticket management")
@SecurityRequirement(name = "bearerAuth")
public class TicketController {

	private final TicketService ticketService;
	
	@PostMapping
	@PreAuthorize("hasAuthority('ticket:create')")      // ABAC — scope only
	@Operation(summary = "Create a ticket",
			description = "Validates assignee via user-service (Feign + Resilience4j)")
	@ApiResponse(responseCode = "201", description = "Ticket created")
	@ApiResponse(responseCode = "400", description = "Validation error")
	public ResponseEntity< TicketResponse > createTicket(
			@Valid @RequestBody CreateTicketRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
				       .body(ticketService.createTicket(request));
	}
	
	@GetMapping
	@PreAuthorize("hasAuthority('ticket:read')")
	@Operation(summary = "List all tickets (paginated)")
	@ApiResponse(responseCode = "200", description = "Tickets listed")
	public ResponseEntity<Page<TicketResponse>> getAllTickets(
			@RequestParam(defaultValue = "0")  int page,
			@RequestParam(defaultValue = "20") int size) {
		return ResponseEntity.ok(ticketService.getAllTickets(page, size));
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ticket:read')")
	@Operation(summary = "Get ticket by ID")
	@ApiResponse(responseCode = "200", description = "Ticket found")
	@ApiResponse(responseCode = "404", description = "Ticket not found")
	public ResponseEntity<TicketResponse> getTicketById(@PathVariable String id) {
		return ResponseEntity.ok(ticketService.getTicketById(id));
	}
	
	@PatchMapping("/{id}/status")
	@PreAuthorize("hasAuthority('ticket:update')")
	@Operation(summary = "Change ticket status",
			description = "Valid transitions: OPEN → IN_PROGRESS → RESOLVED → CLOSED")
	@ApiResponse(responseCode = "200", description = "Status updated")
	@ApiResponse(responseCode = "422", description = "Invalid status transition")
	public ResponseEntity<TicketResponse> changeStatus(
			@PathVariable String id,
			@Valid @RequestBody ChangeStatusRequest request) {
		return ResponseEntity.ok(ticketService.changeStatus(id, request));
	}
	
	@PostMapping("/{id}/comments")
	@PreAuthorize("hasAuthority('ticket:comment')")
	@Operation(summary = "Add comment to ticket")
	@ApiResponse(responseCode = "201", description = "Comment added")
	public ResponseEntity< CommentResponse > addComment(
			@PathVariable String id,
			@Valid @RequestBody AddCommentRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
				       .body(ticketService.addComment(id, request));
	}
}
