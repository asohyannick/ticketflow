package com.boaz.ticket_service.exception;

public class InvalidStatusTransitionException extends RuntimeException {
	public InvalidStatusTransitionException ( String message ) {
		super ( message );
	}
	
	public InvalidStatusTransitionException ( String message , Throwable t ) {
		super ( message , t );
	}
	
	public InvalidStatusTransitionException ( Throwable t ) {
		super ( t );
	}
}
