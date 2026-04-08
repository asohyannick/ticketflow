package com.boaz.ticket_service.config.kafka;
import com.boaz.ticket_service.entity.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@Component
@RequiredArgsConstructor
@Slf4j
public class TicketEventProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	
	@Value("${kafka.producer.topic.ticket-created:ticket.created}")
	private String ticketCreatedTopic;
	
	@Value("${kafka.producer.topic.ticket-status-changed:ticket.status.changed}")
	private String ticketStatusChangedTopic;
	
	public void publishTicketCreated( Ticket ticket) {
		Map<String, Object> event = new HashMap<>();
		event.put("eventType", "TICKET_CREATED");
		event.put("ticketId", ticket.getId());
		event.put("title", ticket.getTitle());
		event.put("priority", ticket.getPriority().name());
		event.put("createdByUserId", ticket.getCreatedByUserId());
		event.put("assigneeId", ticket.getAssigneeId());
		event.put("timestamp", LocalDateTime.now().toString());
		
		kafkaTemplate.send(ticketCreatedTopic, ticket.getId(), event);
		log.info("Published TICKET_CREATED event for ticket: {}", ticket.getId());
	}
	
	public void publishTicketStatusChanged(Ticket ticket, String previousStatus) {
		Map<String, Object> event = new HashMap<>();
		event.put("eventType", "TICKET_STATUS_CHANGED");
		event.put("ticketId", ticket.getId());
		event.put("title", ticket.getTitle());
		event.put("previousStatus", previousStatus);
		event.put("newStatus", ticket.getStatus().name());
		event.put("createdByUserId", ticket.getCreatedByUserId());
		event.put("assigneeId", ticket.getAssigneeId());
		event.put("timestamp", LocalDateTime.now().toString());
		
		kafkaTemplate.send(ticketStatusChangedTopic, ticket.getId(), event);
		log.info("Published TICKET_STATUS_CHANGED for ticket: {} ({} → {})",
				ticket.getId(), previousStatus, ticket.getStatus().name());
	}
}