package com.boaz.notification_service.kafka;
import com.boaz.notification_service.enums.NotificationType;
import com.boaz.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

		private final NotificationService notificationService;

		@KafkaListener(
				topics = "user.created",
				groupId = "notification-group",
				containerFactory = "kafkaListenerContainerFactory"
		)
		public void onUserCreated(Map<String, Object> event) {
			log.info("Received event from [user.created]: {}", event);
			
			try {
				String userId    = (String) event.get("userId");
				String email     = (String) event.get("email");
				String firstName = (String) event.get("firstName");
				String lastName  = (String) event.get("lastName");
				
				if (email == null || userId == null) {
					log.warn("Malformed user.created event — missing email or userId");
					return;
				}
				
				String subject = "Welcome to TicketFlow, " + firstName + "!";
				String body    = String.format(
						"Hi %s %s,\n\n" +
								"Your account has been created successfully.\n" +
								"You can now log in and start creating support tickets.\n\n" +
								"User ID : %s\n" +
								"Email   : %s\n\n" +
								"Best regards,\nThe TicketFlow Team",
						firstName, lastName, userId, email
				);
				
				notificationService.saveAndSend(
						NotificationType.WELCOME_EMAIL,
						email,
						subject,
						body,
						userId,
						"user.created"
				);
				
			} catch (Exception e) {
				log.error("Error processing user.created event: {}", e.getMessage(), e);
			}
		}
		
		@KafkaListener(
				topics = "ticket.created",
				groupId = "notification-group",
				containerFactory = "kafkaListenerContainerFactory"
		)
		public void onTicketCreated(Map<String, Object> event) {
			log.info("Received event from [ticket.created]: {}", event);
			
			try {
				String ticketId        = (String) event.get("ticketId");
				String title           = (String) event.get("title");
				String priority        = (String) event.get("priority");
				String createdByUserId = (String) event.get("createdByUserId");
				String assigneeId      = (String) event.get("assigneeId");
				
				if (ticketId == null || title == null) {
					log.warn("Malformed ticket.created event — missing ticketId or title");
					return;
				}
				
				String supportEmail = "support@ticketflow.com";
				
				String subject = "[NEW TICKET] " + title + " [" + priority + "]";
				String body    = String.format(
						"A new support ticket has been created.\n\n" +
								"Ticket ID    : %s\n" +
								"Title        : %s\n" +
								"Priority     : %s\n" +
								"Created by   : %s\n" +
								"Assigned to  : %s\n\n" +
								"Please log in to the system to handle this ticket.",
						ticketId, title, priority,
						createdByUserId,
						assigneeId != null ? assigneeId : "Unassigned"
				);
				
				notificationService.saveAndSend(
						NotificationType.TICKET_CREATED,
						supportEmail,
						subject,
						body,
						ticketId,
						"ticket.created"
				);
				
			} catch (Exception e) {
				log.error("Error processing ticket.created event: {}", e.getMessage(), e);
			}
		}
		
		@KafkaListener(
				topics = "ticket.status.changed",
				groupId = "notification-group",
				containerFactory = "kafkaListenerContainerFactory"
		)
		public void onTicketStatusChanged(Map<String, Object> event) {
			log.info("Received event from [ticket.status.changed]: {}", event);
			
			try {
				String ticketId        = (String) event.get("ticketId");
				String title           = (String) event.get("title");
				String previousStatus  = (String) event.get("previousStatus");
				String newStatus       = (String) event.get("newStatus");
				String createdByUserId = (String) event.get("createdByUserId");
				
				if (ticketId == null || newStatus == null) {
					log.warn("Malformed ticket.status.changed event — missing required fields");
					return;
				}
				
				String recipientEmail = "user-" + createdByUserId + "@ticketflow.com";
				
				String subject = "[TICKET UPDATE] Your ticket status changed to " + newStatus;
				String body    = String.format(
						"Your support ticket status has been updated.\n\n" +
								"Ticket ID       : %s\n" +
								"Title           : %s\n" +
								"Previous Status : %s\n" +
								"New Status      : %s\n\n" +
								"%s",
						ticketId, title, previousStatus, newStatus,
						resolvedMessage(newStatus)
				);
				
				notificationService.saveAndSend(
						NotificationType.TICKET_STATUS_CHANGED,
						recipientEmail,
						subject,
						body,
						ticketId,
						"ticket.status.changed"
				);
				
			} catch (Exception e) {
				log.error("Error processing ticket.status.changed event: {}", e.getMessage(), e);
			}
		}
		
		private String resolvedMessage(String status) {
			return switch (status) {
				case "IN_PROGRESS" ->
						"An agent is now working on your ticket.";
				case "RESOLVED"    ->
						"Your ticket has been resolved. Please review and close if satisfied.";
				case "CLOSED"      ->
						"Your ticket has been closed. Thank you for using TicketFlow.";
				default            ->
						"Please log in to view the latest status.";
			};
		}
}