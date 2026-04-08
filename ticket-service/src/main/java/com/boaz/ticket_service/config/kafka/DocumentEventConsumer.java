package com.boaz.ticket_service.config.kafka;
import com.boaz.ticket_service.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;
@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentEventConsumer {

		private final TicketRepository ticketRepository;
		
		@KafkaListener(
				topics = "document.uploaded",
				groupId = "ticket-service-group",
				containerFactory = "kafkaListenerContainerFactory"
		)
		public void onDocumentUploaded(Map<String, Object> event) {
			log.info("Received document.uploaded event: {}", event);
			
			String documentId = (String) event.get("documentId");
			String ticketId   = (String) event.get("ticketId");
			
			if (ticketId == null || documentId == null) {
				log.warn("document.uploaded event missing ticketId or documentId — skipping");
				return;
			}
			
			ticketRepository.findById(ticketId).ifPresentOrElse(
					ticket -> {
						if (!ticket.getDocumentIds().contains(documentId)) {
							ticket.getDocumentIds().add(documentId);
							ticketRepository.save(ticket);
							log.info("Linked document {} to ticket {}", documentId, ticketId);
						}
					},
					() -> log.warn("Ticket {} not found for document linking", ticketId)
			);
		}
}