package com.boaz.notification_service.entity;

import com.boaz.notification_service.enums.NotificationStatus;
import com.boaz.notification_service.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

		@Id
		@GeneratedValue(strategy = GenerationType.UUID)
		private String id;
		
		@Enumerated(EnumType.STRING)
		@Column(nullable = false)
		private NotificationType type;
		
		@Enumerated(EnumType.STRING)
		@Column(nullable = false)
		@Builder.Default
		private NotificationStatus status = NotificationStatus.SENT;
		
		@Column(nullable = false)
		private String recipient;
		
		@Column(nullable = false)
		private String subject;
		
		@Column(columnDefinition = "TEXT")
		private String body;
		
		private String referenceId;
		
		private String sourceTopic;
		
		@Column(nullable = false, updatable = false)
		@Builder.Default
		private LocalDateTime createdAt = LocalDateTime.now();
}