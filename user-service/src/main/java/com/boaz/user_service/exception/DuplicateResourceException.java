package com.boaz.user_service.exception;

public class DuplicateResourceException extends RuntimeException {
	public DuplicateResourceException ( String message ) {
		super ( message );
	}
	
	public DuplicateResourceException ( String message , Throwable cause ) {
		super ( message , cause );
	}
	
	public DuplicateResourceException ( Throwable cause ) {
		super ( cause );
	}
}
