package com.boaz.user_service.dto;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
public record AssignRolesRequest(
		@NotEmpty (message = "At least one role name is required")
		Set<String> roleNames
) {
}
