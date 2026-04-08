package com.boaz.document_service.service.documentService;
import com.boaz.document_service.dto.DocumentResponse;
import com.boaz.document_service.dto.PresignedUrlResponse;
import com.boaz.document_service.dto.UploadResponse;
import com.boaz.document_service.entity.Document;
import com.boaz.document_service.exception.ResourceNotFoundException;
import com.boaz.document_service.kafka.DocumentEventProducer;
import com.boaz.document_service.repository.DocumentRepository;
import com.boaz.document_service.service.minioService.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

		private final DocumentRepository documentRepository;
		private final MinioService minioService;
		private final DocumentEventProducer documentEventProducer;
		
		@Value("${minio.bucket-name}")
		private String bucketName;
		
		@Transactional
		public UploadResponse uploadDocument( MultipartFile file, String ticketId) {
			String currentUserId = getCurrentUserId();
			
			String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
			
			minioService.uploadFile(storedFileName, file);
			
			Document document = Document.builder()
					                    .originalFileName(file.getOriginalFilename())
					                    .storedFileName(storedFileName)
					                    .bucketName(bucketName)
					                    .contentType(file.getContentType())
					                    .fileSize(file.getSize())
					                    .ticketId(ticketId)
					                    .uploadedByUserId(currentUserId)
					                    .build();
			
			document = documentRepository.save(document);
			
			documentEventProducer.publishDocumentUploaded(document);
			
			log.info("Document uploaded: {} by user: {} (ticketId={})",
					document.getId(), currentUserId, ticketId);
			
			return new UploadResponse(
					document.getId(),
					document.getOriginalFileName(),
					document.getContentType(),
					document.getFileSize(),
					document.getTicketId(),
					document.getUploadedAt(),
					"File uploaded successfully"
			);
		}
		
		public DocumentResponse getDocumentById( String id) {
			Document document = findById(id);
			
			return new DocumentResponse(
					document.getId(),
					document.getOriginalFileName(),
					document.getContentType(),
					document.getFileSize(),
					document.getTicketId(),
					document.getUploadedByUserId(),
					document.getUploadedAt()
			);
		}
		
		public PresignedUrlResponse getPresignedDownloadUrl( String id) {
			Document document = findById(id);
			
			String presignedUrl = minioService.generatePresignedUrl(
					document.getStoredFileName()
			);
			
			LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
			
			return new PresignedUrlResponse(
					document.getId(),
					presignedUrl,
					expiresAt,
					5
			);
		}
		
		private Document findById(String id) {
			return documentRepository.findById(id)
					       .orElseThrow(() ->
							                    new ResourceNotFoundException ("Document not found: " + id));
		}
		
		private String getCurrentUserId() {
			Authentication auth =
					SecurityContextHolder.getContext().getAuthentication();
			return auth.getName();
		}
}