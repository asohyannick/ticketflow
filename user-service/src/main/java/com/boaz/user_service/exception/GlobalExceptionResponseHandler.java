package com.boaz.user_service.exception;
import java.time.Instant;
public record GlobalExceptionResponseHandler(
		Instant timestamp,
		String message,
		String details,
		String path,
		int status,
		String statusCode,
		String errorCode,
		String method
) { }