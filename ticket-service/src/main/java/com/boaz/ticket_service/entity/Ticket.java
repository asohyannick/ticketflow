package com.boaz.ticket_service.entity;
import com.boaz.ticket_service.constant.TicketPriority;
import com.boaz.ticket_service.constant.TicketStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@Column(nullable = false)
	private String title;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private TicketStatus status = TicketStatus.OPEN;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private TicketPriority priority = TicketPriority.MEDIUM;
	
	@Column(nullable = false)
	private String createdByUserId;
	
	private String assigneeId;
	
	@Column(nullable = false, updatable = false)
	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();
	
	private LocalDateTime updatedAt;
	
	private LocalDateTime resolvedAt;
	
	@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL,
			orphanRemoval = true, fetch = FetchType.LAZY)
	@Builder.Default
	private List<Comment> comments = new ArrayList<>();
	
	@ElementCollection
	@CollectionTable(name = "ticket_documents",
			joinColumns = @JoinColumn(name = "ticket_id"))
	@Column(name = "document_id")
	@Builder.Default
	private List<String> documentIds = new ArrayList<>();
	
	@PreUpdate
	public void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}