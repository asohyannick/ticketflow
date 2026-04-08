package com.boaz.notification_service.controller;

import com.boaz.notification_service.dto.NotificationHistoryResponse;
import com.boaz.notification_service.dto.NotificationResponse;
import com.boaz.notification_service.dto.SendNotificationRequest;
import com.boaz.notification_service.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications Management Endpoints", description = "Notification history and manual send")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

		private final NotificationService notificationService;
		
		@GetMapping("/history")
		@PreAuthorize("hasAuthority('notification:read')")
		@Operation(
				summary = "Get notification history",
				description = "Returns paginated list of all notifications sent by the system"
		)
		@ApiResponse(responseCode = "200", description = "History returned")
		@ApiResponse(responseCode = "403", description = "Missing notification:read scope")
		public ResponseEntity< NotificationHistoryResponse > getHistory(
				@RequestParam(defaultValue = "0")  int page,
				@RequestParam(defaultValue = "20") int size) {
			return ResponseEntity.ok(
					notificationService.getHistory(page, size));
		}
		
		@PostMapping("/send")
		@PreAuthorize("hasAuthority('notification:send')")
		@Operation(
				summary = "Send a manual notification",
				description = "Sends a notification directly without a Kafka event. " +
						              "Useful for testing and admin operations."
		)
		@ApiResponse(responseCode = "201", description = "Notification sent")
		@ApiResponse(responseCode = "400", description = "Validation error")
		@ApiResponse(responseCode = "403", description = "Missing notification:send scope")
		public ResponseEntity< NotificationResponse > sendManual(
				@Valid @RequestBody SendNotificationRequest request) {
			return ResponseEntity.status(HttpStatus.CREATED)
					       .body(notificationService.sendManual(request));
		}
}