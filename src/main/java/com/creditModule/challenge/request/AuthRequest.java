package com.creditModule.challenge.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {

	@NotNull(message = "User Id cannot be null")
	private Long userId;

	@NotNull(message = "Password cannot be null")
	private String password;
}
