package com.boaz.user_service.exception;
public class ForbiddenExceptionHandler extends RuntimeException {
	public ForbiddenExceptionHandler ( String message ) {
		super ( message );
	}
	
	public ForbiddenExceptionHandler ( String message , Throwable cause ) {
		super ( message , cause );
	}
	
	public ForbiddenExceptionHandler ( Throwable cause ) {
		super ( cause );
	}
}
