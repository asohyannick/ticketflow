package com.boaz.user_service.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@Column(unique = true, nullable = false)
	private String name;
	
	private String description;
}