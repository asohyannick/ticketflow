package com.boaz.document_service.exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import java.time.LocalDateTime;
import java.util.Map;
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

		@ExceptionHandler(ResourceNotFoundException.class)
		public ResponseEntity<Map<String, Object>> handleNotFound(
				ResourceNotFoundException ex) {
			return ResponseEntity.status(404).body(Map.of(
					"timestamp", LocalDateTime.now().toString(),
					"status", 404,
					"message", ex.getMessage()
			));
		}
		
		@ExceptionHandler(FileStorageException.class)
		public ResponseEntity<Map<String, Object>> handleStorage(
				FileStorageException ex) {
			log.error("File storage error: {}", ex.getMessage(), ex);
			return ResponseEntity.status(500).body(Map.of(
					"timestamp", LocalDateTime.now().toString(),
					"status", 500,
					"message", ex.getMessage()
			));
		}
		
		@ExceptionHandler(MaxUploadSizeExceededException.class)
		public ResponseEntity<Map<String, Object>> handleMaxSize(
				MaxUploadSizeExceededException ex) {
			return ResponseEntity.status(413).body(Map.of(
					"timestamp", LocalDateTime.now().toString(),
					"status", 413,
					"message", "File size exceeds maximum allowed limit of 50MB"
			));
		}
		
		@ExceptionHandler(Exception.class)
		public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
			log.error("Unexpected error: {}", ex.getMessage(), ex);
			return ResponseEntity.status(500).body(Map.of(
					"timestamp", LocalDateTime.now().toString(),
					"status", 500,
					"message", "An unexpected error occurred"
			));
		}
}