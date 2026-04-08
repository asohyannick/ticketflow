package com.boaz.user_service.service.keyCloak;
import com.boaz.user_service.entity.Role;
import com.boaz.user_service.entity.User;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeyCloakAdminService {

	private final Keycloak keycloak;
	
	@Value("${keycloak.realm}")
	private String realm;
	
	public String createKeycloakUser( User user, String password) {
		UserRepresentation kcUser = new UserRepresentation();
		kcUser.setUsername(user.getEmail());
		kcUser.setEmail(user.getEmail());
		kcUser.setFirstName(user.getFirstName());
		kcUser.setLastName(user.getLastName());
		kcUser.setEnabled(true);
		kcUser.setEmailVerified(true);
		
		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue(password);
		credential.setTemporary(false);
		kcUser.setCredentials(List.of(credential));
		
		Response response = keycloak.realm(realm).users().create(kcUser);
		
		if (response.getStatus() != 201) {
			throw new RuntimeException("Failed to create user in Keycloak: HTTP " + response.getStatus());
		}
		
		String location = response.getHeaderString("Location");
		String keycloakId = location.substring(location.lastIndexOf("/") + 1);
		log.info("Created Keycloak user: {} with ID: {}", user.getEmail(), keycloakId);
		return keycloakId;
	}
	
	public void assignRolesToUser(String keycloakId, Set< Role > roles) {
		UsersResource usersResource = keycloak.realm(realm).users();
		UserResource userResource = usersResource.get(keycloakId);
		
		List<RoleRepresentation> roleReps = roles.stream()
				                                    .map(role -> {
					                                    try {
						                                    return keycloak.realm(realm).roles().get(role.getName()).toRepresentation();
					                                    } catch (Exception e) {
						                                    log.warn("Role {} not found in Keycloak, creating it", role.getName());
						                                    createKeycloakRole(role);
						                                    return keycloak.realm(realm).roles().get(role.getName()).toRepresentation();
					                                    }
				                                    })
				                                    .collect(Collectors.toList());
		
		List<RoleRepresentation> existing = userResource.roles().realmLevel().listAll();
		if (!existing.isEmpty()) {
			userResource.roles().realmLevel().remove(existing);
		}
		
		userResource.roles().realmLevel().add(roleReps);
		log.info("Assigned roles {} to Keycloak user {}",
				roles
						.stream()
						.map(Role::getName)
						.collect(Collectors.joining(", ")), keycloakId);
	}
	
	public void createKeycloakRole(Role role) {
		try {
			keycloak.realm(realm).roles().get(role.getName()).toRepresentation();
			log.debug("Keycloak role {} already exists", role.getName());
		} catch (Exception e) {
			RoleRepresentation roleRep = new RoleRepresentation();
			roleRep.setName(role.getName());
			roleRep.setDescription(role.getDescription());
			
			Map<String, List<String>> attributes = new HashMap<>();
			List<String> permNames = role.getPermissions().stream()
					                         .map(p -> p.getName())
					                         .collect(Collectors.toList());
			attributes.put("permissions", permNames);
			roleRep.setAttributes(attributes);
			
			keycloak.realm(realm).roles().create(roleRep);
			log.info("Created Keycloak role: {}", role.getName());
		}
	}
	
	public void deleteKeycloakUser(String keycloakId) {
		keycloak.realm(realm).users().delete(keycloakId);
		log.info("Deleted Keycloak user: {}", keycloakId);
	}
}