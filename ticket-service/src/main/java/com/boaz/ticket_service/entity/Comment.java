package com.boaz.ticket_service.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "comments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Comment {

	@Id
	@GeneratedValue (strategy = GenerationType.UUID)
	private String id;
	
	@Column (columnDefinition = "TEXT", nullable = false)
	private String content;
	
	@Column(nullable = false)
	private String authorId;
	
	@Column(nullable = false, updatable = false)
	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();
	
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_id", nullable = false)
	private Ticket ticket;
}