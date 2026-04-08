package com.boaz.user_service.dto;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
public record UserResponse(
		String id,
		String email,
		String firstName,
		String lastName,
		boolean active,
		Set <String> roles,
		Set<String> permissions,
		LocalDateTime createdAt
) {}
