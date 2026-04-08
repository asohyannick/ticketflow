package com.boaz.ticket_service.dto;

public record UserDto(
		String id,
		String email ,
		String firstName ,
		String lastName ,
		boolean active ) { }

