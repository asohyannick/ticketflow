package com.boaz.user_service.controller.permission;
import com.boaz.user_service.dto.PermissionResponse;
import com.boaz.user_service.service.permission.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions Management Endpoints", description = "List available permissions")
public class PermissionController {

	private final PermissionService permissionService;
	
	@GetMapping
	@PreAuthorize("hasAuthority('user:read')")
	@Operation(summary = "List all permissions")
	@ApiResponse(responseCode = "200", description = "Permissions listed")
	@ApiResponse(responseCode = "403", description = "Missing user:read scope")
	public ResponseEntity<List< PermissionResponse >> getAllPermissions() {
		return ResponseEntity.ok(permissionService.getAllPermissions());
	}
}