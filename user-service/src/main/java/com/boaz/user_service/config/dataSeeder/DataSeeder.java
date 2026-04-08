package com.boaz.user_service.config.dataSeeder;
import com.boaz.user_service.entity.Permission;
import com.boaz.user_service.entity.Role;
import com.boaz.user_service.repository.RoleRepository;
import com.boaz.user_service.service.keyCloak.KeyCloakAdminService;
import com.boaz.user_service.service.permission.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {
	
	private final PermissionService permissionService;
	private final RoleRepository roleRepository;
	private final KeyCloakAdminService keyCloakAdminService;
	
	@Bean
	public ApplicationRunner seedData() {
		return args -> {
			log.info("Seeding default permissions and roles...");
			
			Permission userCreate     = permissionService.getOrCreate("user:create",     "Create users");
			Permission userRead       = permissionService.getOrCreate("user:read",       "Read users");
			Permission userUpdate     = permissionService.getOrCreate("user:update",     "Update users");
			Permission userDelete     = permissionService.getOrCreate("user:delete",     "Delete users");
			Permission userManageRoles= permissionService.getOrCreate("user:manage-roles","Manage roles");
			Permission ticketCreate   = permissionService.getOrCreate("ticket:create",   "Create tickets");
			Permission ticketRead     = permissionService.getOrCreate("ticket:read",     "Read tickets");
			Permission ticketUpdate   = permissionService.getOrCreate("ticket:update",   "Update tickets");
			Permission ticketDelete   = permissionService.getOrCreate("ticket:delete",   "Delete tickets");
			Permission ticketComment  = permissionService.getOrCreate("ticket:comment",  "Comment on tickets");
			Permission docUpload      = permissionService.getOrCreate("document:upload", "Upload documents");
			Permission docRead        = permissionService.getOrCreate("document:read",   "Read documents");
			Permission docDownload    = permissionService.getOrCreate("document:download","Download documents");
			Permission notifRead      = permissionService.getOrCreate("notification:read","Read notifications");
			Permission notifSend      = permissionService.getOrCreate("notification:send","Send notifications");
			
			seedRole("ADMIN", "Full system access", Set.of(
					userCreate, userRead, userUpdate, userDelete, userManageRoles,
					ticketCreate, ticketRead, ticketUpdate, ticketDelete, ticketComment,
					docUpload, docRead, docDownload,
					notifRead, notifSend
			));
			
			seedRole("AGENT", "Support agent access", Set.of(
					ticketCreate, ticketRead, ticketUpdate, ticketComment,
					docUpload, docRead, docDownload,
					notifRead
			));
			
			seedRole("USER", "Standard user access", Set.of(
					ticketCreate, ticketRead, ticketComment,
					docUpload, docRead, docDownload
			));
			
			log.info("Data seeding complete.");
		};
	}
	
	private void seedRole(String name, String description, Set<Permission> permissions) {
		if (!roleRepository.existsByName(name)) {
			Role role = Role.builder()
					            .name(name)
					            .description(description)
					            .permissions(permissions)
					            .build();
			role = roleRepository.save(role);
			
			try {
				keyCloakAdminService.createKeycloakRole(role);
			} catch (Exception e) {
				log.warn("Could not create role {} in Keycloak: {}", name, e.getMessage());
			}
			
			log.info("Seeded role: {}", name);
		}
	}
}