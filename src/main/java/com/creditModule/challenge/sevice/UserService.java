package com.creditModule.challenge.sevice;

import com.creditModule.challenge.entities.User;

import javax.naming.AuthenticationException;

public interface UserService {
	User authenticate(Long userId, String password) throws AuthenticationException;
}
