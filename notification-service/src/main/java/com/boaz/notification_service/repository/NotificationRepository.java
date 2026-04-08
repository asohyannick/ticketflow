package com.boaz.notification_service.repository;
import com.boaz.notification_service.entity.Notification;
import com.boaz.notification_service.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository< Notification, String> {

	Page<Notification> findAllByOrderByCreatedAtDesc(Pageable pageable);
	
	Page<Notification> findByRecipientOrderByCreatedAtDesc(
			String recipient, Pageable pageable);
	
	Page<Notification> findByTypeOrderByCreatedAtDesc(
			NotificationType type, Pageable pageable);
}