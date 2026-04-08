package com.boaz.user_service.controller.role;
import com.boaz.user_service.dto.AssignPermissionsRequest;
import com.boaz.user_service.dto.CreateRoleRequest;
import com.boaz.user_service.dto.RoleResponse;
import com.boaz.user_service.service.role.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Role management")
public class RoleController {

	private final RoleService roleService;
	
	@PostMapping
	@PreAuthorize("hasAuthority('user:manage-roles')")
	@Operation(summary = "Create a new role")
	@ApiResponse(responseCode = "201", description = "Role created")
	@ApiResponse(responseCode = "409", description = "Role already exists")
	public ResponseEntity< RoleResponse > createRole( @Valid @RequestBody CreateRoleRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
				       .body(roleService.createRole(request));
	}
	
	@GetMapping
	@PreAuthorize("hasAuthority('user:read')")
	@Operation(summary = "List all roles")
	@ApiResponse(responseCode = "200", description = "Roles listed")
	public ResponseEntity<List<RoleResponse>> getAllRoles() {
		return ResponseEntity.ok(roleService.getAllRoles());
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('user:read')")
	@Operation(summary = "Get role by ID")
	@ApiResponse(responseCode = "200", description = "Role found")
	@ApiResponse(responseCode = "404", description = "Role not found")
	public ResponseEntity<RoleResponse> getRoleById(@PathVariable String id) {
		return ResponseEntity.ok(roleService.getRoleById(id));
	}
	
	@PutMapping("/{id}/permissions")
	@PreAuthorize("hasAuthority('user:manage-roles')")
	@Operation(summary = "Assign permissions to a role")
	@ApiResponse(responseCode = "200", description = "Permissions assigned")
	@ApiResponse(responseCode = "404", description = "Role or permission not found")
	public ResponseEntity<RoleResponse> assignPermissions(
			@PathVariable String id,
			@Valid @RequestBody AssignPermissionsRequest request) {
		return ResponseEntity.ok(roleService.assignPermissionsToRole(id, request));
	}
}