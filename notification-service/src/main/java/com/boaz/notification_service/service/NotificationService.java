package com.boaz.notification_service.service;
import com.boaz.notification_service.dto.NotificationHistoryResponse;
import com.boaz.notification_service.dto.NotificationResponse;
import com.boaz.notification_service.dto.SendNotificationRequest;
import com.boaz.notification_service.entity.Notification;
import com.boaz.notification_service.enums.NotificationStatus;
import com.boaz.notification_service.enums.NotificationType;
import com.boaz.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

		private final NotificationRepository notificationRepository;
		
		@Transactional
		public NotificationResponse saveAndSend(
				NotificationType type,
				String recipient,
				String subject,
				String body,
				String referenceId,
				String sourceTopic) {
			
			log.info("========================================");
			log.info("[NOTIFICATION SENT]");
			log.info("  Type      : {}", type);
			log.info("  To        : {}", recipient);
			log.info("  Subject   : {}", subject);
			log.info("  Body      : {}", body);
			log.info("  Reference : {}", referenceId);
			log.info("  Source    : {}", sourceTopic);
			log.info("========================================");
			
			Notification notification = Notification.builder()
					                            .type(type)
					                            .status( NotificationStatus.SENT)
					                            .recipient(recipient)
					                            .subject(subject)
					                            .body(body)
					                            .referenceId(referenceId)
					                            .sourceTopic(sourceTopic)
					                            .build();
			
			notification = notificationRepository.save(notification);
			return toResponse(notification);
		}
		
		public NotificationHistoryResponse getHistory( int page, int size) {
			Pageable pageable = PageRequest.of(page, size,
					Sort.by(Sort.Direction.DESC, "createdAt"));
			
			Page<Notification> result =
					notificationRepository.findAllByOrderByCreatedAtDesc(pageable);
			
			List<NotificationResponse> notifications = result
					                                           .getContent()
					                                           .stream()
					                                           .map(this::toResponse)
					                                           .toList();
			
			return new NotificationHistoryResponse(
					notifications,
					result.getTotalElements(),
					result.getTotalPages(),
					result.getNumber(),
					result.getSize()
			);
		}
		
		@Transactional
		public NotificationResponse sendManual( SendNotificationRequest request) {
			return saveAndSend(
					NotificationType.MANUAL,
					request.recipient(),
					request.subject(),
					request.body(),
					null,
					null
			);
		}
		
		private NotificationResponse toResponse(Notification n) {
			return new NotificationResponse(
					n.getId(),
					n.getType(),
					n.getStatus(),
					n.getRecipient(),
					n.getSubject(),
					n.getBody(),
					n.getReferenceId(),
					n.getSourceTopic(),
					n.getCreatedAt()
			);
		}
}