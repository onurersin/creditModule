package com.creditModule.challenge.controller;

import com.creditModule.challenge.entities.User;
import com.creditModule.challenge.request.AuthRequest;
import com.creditModule.challenge.sevice.UserService;
import com.creditModule.challenge.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class AuthController {

	private UserService userDetailsService;

	private JwtUtil jwtUtil;

	@PostMapping("/login")
	public String login(@Valid @RequestBody AuthRequest authRequest) throws AuthenticationException {
		User user = userDetailsService.authenticate(authRequest.getUserId(), authRequest.getPassword());
		return jwtUtil.generateToken(user.getId(), user.getCustomerId(), user.getRole());
	}
}
