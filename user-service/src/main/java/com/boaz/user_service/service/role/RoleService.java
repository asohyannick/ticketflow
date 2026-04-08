package com.boaz.user_service.service.role;
import com.boaz.user_service.dto.AssignPermissionsRequest;
import com.boaz.user_service.dto.CreateRoleRequest;
import com.boaz.user_service.dto.RoleResponse;
import com.boaz.user_service.entity.Permission;
import com.boaz.user_service.entity.Role;
import com.boaz.user_service.exception.DuplicateResourceException;
import com.boaz.user_service.exception.ResourceNotFoundException;
import com.boaz.user_service.repository.PermissionRepository;
import com.boaz.user_service.repository.RoleRepository;
import com.boaz.user_service.service.keyCloak.KeyCloakAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
		
		private final RoleRepository roleRepository;
		private final PermissionRepository permissionRepository;
		private final KeyCloakAdminService keyCloakAdminService;
		
		@Transactional
		public RoleResponse createRole( CreateRoleRequest request) {
			if (roleRepository.existsByName(request.name())) {
				throw new DuplicateResourceException ("Role already exists: " + request.name());
			}
			
			Role role = Role.builder()
					            .name(request.name().toUpperCase())
					            .description(request.description())
					            .build();
			
			role = roleRepository.save(role);
			
			keyCloakAdminService.createKeycloakRole(role);
			
			log.info("Created role: {}", role.getName());
			return toResponse(role);
		}
		
		public List<RoleResponse> getAllRoles() {
			return roleRepository.findAll()
					       .stream()
					       .map(this::toResponse)
					       .collect(Collectors.toList());
		}
		
		public RoleResponse getRoleById(String id) {
			Role role = roleRepository.findById(id)
					            .orElseThrow(() -> new ResourceNotFoundException ("Role not found: " + id));
			return toResponse(role);
		}
		
		@Transactional
		public RoleResponse assignPermissionsToRole(String roleId, AssignPermissionsRequest request) {
			Role role = roleRepository.findById(roleId)
					            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleId));
			
			Set< Permission > permissions = permissionRepository.findByNameIn(request.permissionNames());
			
			if (permissions.size() != request.permissionNames().size()) {
				throw new ResourceNotFoundException("One or more permissions not found");
			}
			
			role.setPermissions(permissions);
			role = roleRepository.save(role);
			
			keyCloakAdminService.createKeycloakRole(role);
			
			log.info("Assigned {} permissions to role {}", permissions.size(), role.getName());
			return toResponse(role);
		}

		public RoleResponse toResponse(Role role) {
			return new RoleResponse(
					role.getId(),
					role.getName(),
					role.getDescription(),
					role.getPermissions().stream()
							.map(Permission::getName)
							.collect(Collectors.toSet())
			);
		}
}