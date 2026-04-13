package com.boaz.document_service.controller;
import com.boaz.document_service.dto.DocumentResponse;
import com.boaz.document_service.dto.PresignedUrlResponse;
import com.boaz.document_service.dto.UploadResponse;
import com.boaz.document_service.service.documentService.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Tag(name = "Documents Management Endpoints", description = "File upload and download management")
@SecurityRequirement(name = "bearerAuth")
public class DocumentController {

		private final DocumentService documentService;
		
		@PostMapping(
				value = "/upload",
				consumes = MediaType.MULTIPART_FORM_DATA_VALUE
		)
		@PreAuthorize("hasAuthority('document:upload')")
		@Operation(
				summary = "Upload a document",
				description = "Uploads file to MinIO, stores metadata in DB, " +
						              "publishes document.uploaded Kafka event"
		)
		@ApiResponse(responseCode = "201", description = "File uploaded successfully")
		@ApiResponse(responseCode = "400", description = "No file provided")
		@ApiResponse(responseCode = "413", description = "File exceeds 50MB limit")
		public ResponseEntity< UploadResponse > uploadDocument(
				@RequestPart("file") MultipartFile file,
				@RequestParam(required = false) String ticketId
		) {
			
			if (file.isEmpty()) {
				return ResponseEntity.badRequest().build();
			}
			
			UploadResponse response = documentService.uploadDocument(file, ticketId);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}
		
		@GetMapping("/{id}")
		@PreAuthorize("hasAuthority('document:read')")
		@Operation(
				summary = "Get document metadata",
				description = "Returns name, size, MIME type and upload date — NOT the file itself"
		)
		@ApiResponse(responseCode = "200", description = "Metadata returned")
		@ApiResponse(responseCode = "404", description = "Document not found")
		public ResponseEntity< DocumentResponse > getDocumentMetadata(
				@PathVariable String id
		) {
			return ResponseEntity.ok(documentService.getDocumentById(id));
		}
		
		@GetMapping("/{id}/download")
		@PreAuthorize("hasAuthority('document:download')")
		@Operation(
				summary = "Get pre-signed download URL",
				description = "Returns a MinIO pre-signed URL valid for 5 minutes. " +
						              "The URL can be used in a browser without any token."
		)
		@ApiResponse(responseCode = "200", description = "Pre-signed URL returned")
		@ApiResponse(responseCode = "404", description = "Document not found")
		public ResponseEntity< PresignedUrlResponse > getPresignedDownloadUrl(
				@PathVariable String id
		) {
			return ResponseEntity.ok(documentService.getPresignedDownloadUrl(id));
		}
}