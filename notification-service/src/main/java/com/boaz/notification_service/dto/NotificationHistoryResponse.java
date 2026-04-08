package com.boaz.notification_service.dto;
import java.util.List;
public record NotificationHistoryResponse(
		List<NotificationResponse> notifications,
		long totalElements,
		int totalPages,
		int currentPage,
		int pageSize
) {}