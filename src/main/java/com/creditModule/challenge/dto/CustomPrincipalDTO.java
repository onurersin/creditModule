package com.creditModule.challenge.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomPrincipalDTO {
	private Long userId;
	private Long customerId;
	private String role;

	public CustomPrincipalDTO(Long userId, Long customerId, String role) {
		this.userId = userId;
		this.customerId = customerId;
		this.role = role;
	}
}
