package com.boaz.user_service.exception;

public class UnAuthorizedExceptionHandler extends RuntimeException {
	public UnAuthorizedExceptionHandler ( String message ) {
		super ( message );
	}
	public UnAuthorizedExceptionHandler ( String message , Throwable cause ) {
		super ( message , cause );
	}
	public UnAuthorizedExceptionHandler ( Throwable cause ) {
		super ( cause );
	}
}
