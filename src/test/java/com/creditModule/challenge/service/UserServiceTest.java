package com.creditModule.challenge.service;


import com.creditModule.challenge.entities.User;
import com.creditModule.challenge.exception.ResourceNotFoundException;
import com.creditModule.challenge.repository.UserRepository;
import com.creditModule.challenge.sevice.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.AuthenticationException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserServiceImpl userService;
	@Mock
	private UserRepository userRepositoryMock;
	@Mock
	private PasswordEncoder passwordEncoderMock;

	private final static String PASSWORD = "password";

	private final static Long USER_ID = 1L;

	@Test
	public void testAuthenticate_whenUserNameAndPasswordMatch_ReturnUser() throws AuthenticationException {
		//arrange
		User user = User.builder().id(USER_ID).password(PASSWORD).build();
		when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.of(user));
		when(passwordEncoderMock.matches(any(), any())).thenReturn(Boolean.TRUE);

		//act
		userService.authenticate(USER_ID, PASSWORD);

		//assert
		assertEquals(USER_ID, user.getId());
	}

	@Test
	public void testAuthenticate_whenUserNotFound_throwException() throws AuthenticationException {
		//arrange
		when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.empty());

		//act assert
		assertThrows(ResourceNotFoundException.class, () -> userService.authenticate(USER_ID, PASSWORD));
	}

	@Test
	public void testAuthenticate_whenUserNameAndPasswordIsNotMatch_throwException() throws AuthenticationException {
		//arrange
		User user = User.builder().id(USER_ID).password(PASSWORD).build();
		when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.of(user));
		when(passwordEncoderMock.matches(any(), any())).thenReturn(Boolean.FALSE);

		//act assert
		assertThrows(BadCredentialsException.class, () -> userService.authenticate(USER_ID, PASSWORD));
	}
}
