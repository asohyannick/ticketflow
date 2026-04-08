package com.boaz.ticket_service.service;
import com.boaz.ticket_service.config.kafka.TicketEventProducer;
import com.boaz.ticket_service.constant.TicketPriority;
import com.boaz.ticket_service.constant.TicketStatus;
import com.boaz.ticket_service.dto.*;
import com.boaz.ticket_service.entity.Comment;
import com.boaz.ticket_service.entity.Ticket;
import com.boaz.ticket_service.exception.InvalidStatusTransitionException;
import com.boaz.ticket_service.exception.ResourceNotFoundException;
import com.boaz.ticket_service.feign.UserServiceClient;
import com.boaz.ticket_service.repository.CommentRepository;
import com.boaz.ticket_service.repository.TicketRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

		private final TicketRepository ticketRepository;
		private final CommentRepository commentRepository;
		private final UserServiceClient userServiceClient;
		private final TicketEventProducer ticketEventProducer;
		
		@Transactional
		@CircuitBreaker(name = "user-service", fallbackMethod = "createTicketFallback")
		@Retry(name = "user-service")
		public TicketResponse createTicket( CreateTicketRequest request) {
			String currentUserId = getCurrentUserId();
			

			if (request.assigneeId() != null) {
				UserDto assignee = userServiceClient.getUserById(request.assigneeId());
				log.info("Validated assignee: {} ({})", assignee.id(), assignee.email());
			}
			
			Ticket ticket = Ticket.builder()
					                .title(request.title())
					                .description(request.description())
					                .priority(request.priority() != null
							                          ? request.priority() : TicketPriority.MEDIUM)
					                .status( TicketStatus.OPEN)
					                .createdByUserId(currentUserId)
					                .assigneeId(request.assigneeId())
					                .build();
			
			ticket = ticketRepository.save(ticket);
			
			ticketEventProducer.publishTicketCreated(ticket);
			
			log.info("Created ticket: {} by user: {}", ticket.getId(), currentUserId);
			return toResponse(ticket);
		}
		
		public TicketResponse createTicketFallback(
				CreateTicketRequest request, Throwable throwable) {
			log.warn("FALLBACK createTicket triggered: {}", throwable.getMessage());
			String currentUserId = getCurrentUserId();
			Ticket ticket = Ticket.builder()
					                .title(request.title())
					                .description(request.description())
					                .priority(request.priority() != null
							                          ? request.priority () : TicketPriority.MEDIUM)
					                .status(TicketStatus.OPEN)
					                .createdByUserId(currentUserId)
					                .assigneeId(request.assigneeId())
					                .build();
			ticket = ticketRepository.save(ticket);
			ticketEventProducer.publishTicketCreated(ticket);
			log.warn("Ticket {} created without assignee validation (user-service unavailable)",
					ticket.getId());
			return toResponse(ticket);
		}
		
		public Page<TicketResponse> getAllTickets(int page, int size) {
			Pageable pageable = PageRequest.of(page, size,
					Sort.by(Sort.Direction.DESC, "createdAt"));
			return ticketRepository.findAll(pageable).map(this::toResponse);
		}
		
		public TicketResponse getTicketById(String id) {
			Ticket ticket = findTicketById(id);
			return toResponse(ticket);
		}
		
		@Transactional
		public TicketResponse changeStatus(String ticketId, ChangeStatusRequest request) {
			Ticket ticket = findTicketById(ticketId);
			
			String previousStatus = ticket.getStatus().name();
			TicketStatus newStatus = request.status();
			
			validateStatusTransition(ticket.getStatus(), newStatus);
			
			ticket.setStatus(newStatus);
			
			if (newStatus == TicketStatus.RESOLVED) {
				ticket.setResolvedAt(LocalDateTime.now());
			}
			
			ticket = ticketRepository.save(ticket);
			
			ticketEventProducer.publishTicketStatusChanged(ticket, previousStatus);
			
			log.info("Ticket {} status changed: {} → {}", ticketId, previousStatus, newStatus);
			return toResponse(ticket);
		}
		
		@Transactional
		public CommentResponse addComment( String ticketId, AddCommentRequest request) {
			Ticket ticket = findTicketById(ticketId);
			String currentUserId = getCurrentUserId();
			
			Comment comment = Comment.builder()
					                  .content(request.content ())
					                  .authorId(currentUserId)
					                  .ticket(ticket)
					                  .build();
			
			comment = commentRepository.save(comment);
			log.info("Added comment to ticket {} by user {}", ticketId, currentUserId);
			return toCommentResponse(comment);
		}
		
		private void validateStatusTransition(TicketStatus current, TicketStatus next) {
			boolean valid = switch (current) {
				case OPEN        -> next == TicketStatus.IN_PROGRESS;
				case IN_PROGRESS -> next == TicketStatus.RESOLVED;
				case RESOLVED    -> next == TicketStatus.CLOSED;
				case CLOSED      -> false;   // closed is terminal
			};
			
			if (!valid) {
				throw new InvalidStatusTransitionException (
						String.format("Invalid transition: %s → %s. " +
								              "Valid chain: OPEN → IN_PROGRESS → RESOLVED → CLOSED",
								current, next));
			}
		}
		
		
		private Ticket findTicketById(String id) {
			return ticketRepository.findById(id)
					       .orElseThrow(() -> new ResourceNotFoundException ("Ticket not found: " + id));
		}
		
		private String getCurrentUserId() {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			return auth.getName();
		}

		private TicketResponse toResponse(Ticket ticket) {
			List<CommentResponse> comments = ticket.getComments()
					                                 .stream()
					                                 .map(this::toCommentResponse)
					                                 .toList();
			
			return new TicketResponse(
					ticket.getId(),
					ticket.getTitle(),
					ticket.getDescription(),
					ticket.getStatus(),
					ticket.getPriority(),
					ticket.getAssigneeId(),
					ticket.getCreatedByUserId(),
					ticket.getCreatedAt(),
					ticket.getUpdatedAt(),
					ticket.getResolvedAt(),
					comments,
					ticket.getDocumentIds()
			);
		}

	private CommentResponse toCommentResponse(Comment comment) {
		return new CommentResponse(
				comment.getId(),
				comment.getContent(),
				comment.getAuthorId(),
				comment.getCreatedAt()
		);
	}
}