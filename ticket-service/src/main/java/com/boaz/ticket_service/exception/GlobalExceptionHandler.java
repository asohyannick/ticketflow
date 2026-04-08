package com.boaz.ticket_service.exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(
			MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(e ->
				                                             errors.put(((FieldError) e).getField(), e.getDefaultMessage()));
		return ResponseEntity.badRequest().body(Map.of(
				"timestamp", LocalDateTime.now().toString(),
				"status", 400, "errors", errors));
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleNotFound(
			ResourceNotFoundException ex) {
		return ResponseEntity.status(404).body(Map.of(
				"timestamp", LocalDateTime.now().toString(),
				"status", 404, "message", ex.getMessage()));
	}
	
	@ExceptionHandler(InvalidStatusTransitionException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidTransition(
			InvalidStatusTransitionException ex) {
		return ResponseEntity.status(422).body(Map.of(
				"timestamp", LocalDateTime.now().toString(),
				"status", 422, "message", ex.getMessage()));
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
		log.error("Unexpected error: {}", ex.getMessage(), ex);
		return ResponseEntity.status(500).body(Map.of(
				"timestamp", LocalDateTime.now().toString(),
				"status", 500, "message", "An unexpected error occurred"));
	}
}