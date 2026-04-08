package com.boaz.user_service.dto;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
public record AssignPermissionsRequest(
		
		@NotEmpty (message = "At least one permission is required")
		Set <String> permissionNames
) {
}
