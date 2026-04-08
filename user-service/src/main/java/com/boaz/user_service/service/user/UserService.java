package com.boaz.user_service.service.user;
import com.boaz.user_service.dto.AssignRolesRequest;
import com.boaz.user_service.dto.CreateUserRequest;
import com.boaz.user_service.dto.UserResponse;
import com.boaz.user_service.entity.Permission;
import com.boaz.user_service.entity.Role;
import com.boaz.user_service.entity.User;
import com.boaz.user_service.exception.DuplicateResourceException;
import com.boaz.user_service.exception.ResourceNotFoundException;
import com.boaz.user_service.repository.RoleRepository;
import com.boaz.user_service.repository.UserRepository;
import com.boaz.user_service.service.keyCloak.KeyCloakAdminService;
import com.boaz.user_service.service.keyCloak.UserEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

		private final UserRepository userRepository;
		private final RoleRepository roleRepository;
		private final KeyCloakAdminService keycloakAdminService;
		private final UserEventProducer userEventProducer;
		
		@Transactional
		public UserResponse createUser( CreateUserRequest request) {
			if (userRepository.existsByEmail(request.email())) {
				throw new DuplicateResourceException (
						"User already exists with email: " + request.email());
			}
			
			User user = User.builder()
					            .email(request.email())
					            .firstName(request.firstName())
					            .lastName(request.lastName())
					            .build();
			
			user = userRepository.save(user);
			
			String keycloakId = keycloakAdminService.createKeycloakUser(user, request.password());
			user.setKeycloakId(keycloakId);
			user = userRepository.save(user);
			userEventProducer.publishUserCreated(user);
			log.info("Created user: {} (keycloakId={})", user.getEmail(), keycloakId);
			return toResponse(user);
		}
		
		public UserResponse getUserById(String id) {
			User user = userRepository.findById(id)
					            .orElseThrow(() -> new ResourceNotFoundException ("User not found: " + id));
			return toResponse(user);
		}
		
		@Transactional
		public UserResponse assignRolesToUser(String userId, AssignRolesRequest request) {
			User user = userRepository.findById(userId)
					            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
			
			Set< Role > roles = roleRepository.findByNameIn(request.roleNames());
			
			if (roles.size() != request.roleNames().size()) {
				Set<String> foundNames = roles.stream()
						                         .map(Role::getName).collect(Collectors.toSet());
				Set<String> missing = new HashSet<>(request.roleNames());
				missing.removeAll(foundNames);
				throw new ResourceNotFoundException("Roles not found: " + missing);
			}
			
			user.setRoles(roles);
			user = userRepository.save(user);
			
			if (user.getKeycloakId() != null) {
				keycloakAdminService.assignRolesToUser(user.getKeycloakId(), roles);
			}
			
			log.info("Assigned roles {} to user {}",
					roles.stream().map(Role::getName).collect(Collectors.joining(", ")),
					user.getEmail());
			
			return toResponse(user);
		}

		private UserResponse toResponse(User user) {
			Set<String> allPermissions = user.getAllPermissions()
					                             .stream()
					                             .map(Permission::getName)
					                             .collect(Collectors.toSet());
			
			Set<String> roleNames = user.getRoles()
					                        .stream()
					                        .map(Role::getName)
					                        .collect(Collectors.toSet());
			
			return new UserResponse(
					user.getId(),
					user.getEmail(),
					user.getFirstName(),
					user.getLastName(),
					user.isActive(),
					roleNames,
					allPermissions,
					user.getCreatedAt()
			);
		}
}