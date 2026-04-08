package com.boaz.document_service.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "documents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Document {

		@Id
		@GeneratedValue(strategy = GenerationType.UUID)
		private String id;
		
		@Column(nullable = false)
		private String originalFileName;
		
		@Column(nullable = false)
		private String storedFileName;
		
		@Column(nullable = false)
		private String bucketName;
		
		@Column(nullable = false)
		private String contentType;
		
		@Column(nullable = false)
		private Long fileSize;
		
		private String ticketId;
		
		@Column(nullable = false)
		private String uploadedByUserId;
		
		@Column(nullable = false, updatable = false)
		@Builder.Default
		private LocalDateTime uploadedAt = LocalDateTime.now();
}