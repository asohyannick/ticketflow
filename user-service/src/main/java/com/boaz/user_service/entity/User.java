package com.boaz.user_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

		@Id
		@GeneratedValue(strategy = GenerationType.UUID)
		private String id;
		
		@Column(unique = true, nullable = false)
		private String email;
		
		@Column(nullable = false)
		private String firstName;
		
		@Column(nullable = false)
		private String lastName;
		
		private String keycloakId;
		
		@Column(nullable = false)
		@Builder.Default
		private boolean active = true;
		
		@Column(nullable = false, updatable = false)
		@Builder.Default
		private LocalDateTime createdAt = LocalDateTime.now();
		
		@ManyToMany(fetch = FetchType.EAGER)
		@JoinTable(
				name = "user_roles",
				joinColumns = @JoinColumn(name = "user_id"),
				inverseJoinColumns = @JoinColumn(name = "role_id")
		)
		@Builder.Default
		private Set<Role> roles = new HashSet<>();
		
		public Set<Permission> getAllPermissions() {
			Set<Permission> allPermissions = new HashSet<>();
			for (Role role : roles) {
				allPermissions.addAll(role.getPermissions());
			}
			return allPermissions;
		}
}