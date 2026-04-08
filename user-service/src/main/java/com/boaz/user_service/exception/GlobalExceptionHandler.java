package com.boaz.user_service.exception;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.time.Instant;
@RestControllerAdvice
public class GlobalExceptionHandler {

private ResponseEntity<GlobalExceptionResponseHandler> buildResponse(
		String message,
		String details,
		String statusCode,
		String errorCode,
		HttpStatus status,
		HttpServletRequest request
) {
	
	GlobalExceptionResponseHandler response =
			new GlobalExceptionResponseHandler(
					Instant.now(),
					message,
					details,
					statusCode,
					status.value(),
					errorCode,
					request.getRequestURI(),
					request.getMethod()
			);
	
	return ResponseEntity.status(status).body(response);
}

@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<GlobalExceptionResponseHandler> handleNotFoundException(
		NoHandlerFoundException ex,
		HttpServletRequest request
) {
	return buildResponse(
			ex.getMessage(),
			"Resource not found!",
			HttpStatus.NOT_FOUND.getReasonPhrase(),
			"NOT_FOUND",
			HttpStatus.NOT_FOUND,
			request
	);
}

@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<GlobalExceptionResponseHandler> handleValidationException(
		MethodArgumentNotValidException ex,
		HttpServletRequest request
) {
	String errorMessage = ex.getBindingResult()
			                      .getFieldErrors()
			                      .stream()
			                      .map(error -> error.getField() + ": " + error.getDefaultMessage())
			                      .findFirst()
			                      .orElse("Validation failed");
	
	return buildResponse(
			errorMessage,
			"Invalid request payload",
			HttpStatus.BAD_REQUEST.getReasonPhrase(),
			"BAD_REQUEST",
			HttpStatus.BAD_REQUEST,
			request
	);
}

@ExceptionHandler(UnAuthorizedExceptionHandler.class)
public ResponseEntity<GlobalExceptionResponseHandler> handleUnauthorizedException(
		UnAuthorizedExceptionHandler ex,
		HttpServletRequest request
) {
	return buildResponse(
			ex.getMessage(),
			"Authentication failed",
			HttpStatus.UNAUTHORIZED.getReasonPhrase(),
			"UNAUTHORIZED",
			HttpStatus.UNAUTHORIZED,
			request
	);
}

@ExceptionHandler(ForbiddenExceptionHandler.class)
public ResponseEntity<GlobalExceptionResponseHandler> handleAccessDeniedException(
		ForbiddenExceptionHandler ex,
		HttpServletRequest request
) {
	return buildResponse(
			ex.getMessage(),
			"Access denied",
			HttpStatus.FORBIDDEN.getReasonPhrase(),
			"FORBIDDEN",
			HttpStatus.FORBIDDEN,
			request
	);
}

@ExceptionHandler(DuplicateResourceException.class)
public ResponseEntity<GlobalExceptionResponseHandler> handleConflictException(
		DuplicateResourceException ex,
		HttpServletRequest request
) {
	return buildResponse(
			ex.getMessage(),
			"Conflict occurred",
			HttpStatus.CONFLICT.getReasonPhrase(),
			"CONFLICT",
			HttpStatus.CONFLICT,
			request
	);
}

@ExceptionHandler(InternalServalErrorExceptionHandler.class)
public ResponseEntity<GlobalExceptionResponseHandler> handleGlobalException(
		InternalServalErrorExceptionHandler ex,
		HttpServletRequest request
) {
	return buildResponse(
			ex.getMessage(),
			"An unexpected error occurred",
			HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
			"INTERNAL_SERVER_ERROR",
			HttpStatus.INTERNAL_SERVER_ERROR,
			request
	);
}
}