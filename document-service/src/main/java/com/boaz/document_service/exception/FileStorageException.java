package com.boaz.document_service.exception;
public class FileStorageException extends RuntimeException {
	public FileStorageException ( String message ) {
		super ( message );
	}
	public FileStorageException ( String message, Throwable cause ) {
		super ( message, cause );
	}
	
	public FileStorageException ( Throwable cause ) {
		super ( cause );
	}
}
