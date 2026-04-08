package com.boaz.user_service.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public record CreateUserRequest(
	
		@NotBlank(message = "First name is required")
		String firstName,
		
		@NotBlank(message = "Last name is required")
		String lastName,
		
		@Email (message = "Must be a valid email")
		@NotBlank(message = "Email is required")
		String email,
		
		@NotBlank (message = "Password is required")
		String password
) {
}
