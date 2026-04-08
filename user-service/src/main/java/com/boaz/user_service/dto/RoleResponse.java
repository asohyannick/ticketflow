package com.boaz.user_service.dto;
import java.util.Set;
public record RoleResponse(
		String id,
		String name,
		String description,
		Set <String> permissions
) {}
