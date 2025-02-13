package com.creditModule.challenge.sevice;

import com.creditModule.challenge.constant.ExceptionTypes;
import com.creditModule.challenge.entities.User;
import com.creditModule.challenge.exception.ResourceNotFoundException;
import com.creditModule.challenge.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;

	public User authenticate(Long userId, String password) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(ExceptionTypes.USER_NOT_FOUND.getMessage(), userId)));

		boolean isMatch = passwordEncoder.matches(password, user.getPassword());
		if (!isMatch) {
			throw new BadCredentialsException(ExceptionTypes.INVALID_CREDENTIALS.getMessage());
		}

		return user;
	}
}
