package com.boaz.user_service.controller.user;
import com.boaz.user_service.dto.AssignRolesRequest;
import com.boaz.user_service.dto.CreateUserRequest;
import com.boaz.user_service.dto.UserResponse;
import com.boaz.user_service.service.user.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users Management Endpoints", description = "User management")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
			
			private final UserService userService;
			
			@PostMapping
			@PreAuthorize("hasAuthority('user:create')")     // ← ABAC scope check
			@Operation(summary = "Create a new user",
					description = "Creates user in local DB + Keycloak, publishes user.created event")
			@ApiResponse(responseCode = "201", description = "User created successfully")
			@ApiResponse(responseCode = "400", description = "Validation error")
			@ApiResponse(responseCode = "409", description = "Email already in use")
			public ResponseEntity< UserResponse > createUser( @Valid @RequestBody CreateUserRequest request) {
				return ResponseEntity.status(HttpStatus.CREATED)
						       .body(userService.createUser(request));
			}
			
			@GetMapping("/{id}")
			@PreAuthorize("hasAuthority('user:read')")
			@Operation(summary = "Get user by ID",
					description = "Returns user with their roles and full permission union")
			@ApiResponse(responseCode = "200", description = "User found")
			@ApiResponse(responseCode = "404", description = "User not found")
			public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
				return ResponseEntity.ok(userService.getUserById(id));
			}
			
			@PutMapping("/{id}/roles")
			@PreAuthorize("hasAuthority('user:manage-roles')")
			@Operation(summary = "Assign roles to a user",
					description = "Replaces all existing roles. Syncs to Keycloak immediately.")
			@ApiResponse(responseCode = "200", description = "Roles assigned, Keycloak synced")
			@ApiResponse(responseCode = "404", description = "User or role not found")
			public ResponseEntity<UserResponse> assignRoles(
					@PathVariable String id,
					@Valid @RequestBody AssignRolesRequest request) {
				return ResponseEntity.ok(userService.assignRolesToUser(id, request));
			}
}