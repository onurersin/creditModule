package com.creditModule.challenge.util;

import com.creditModule.challenge.constant.Role;
import com.creditModule.challenge.dto.CustomPrincipalDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

	public static boolean isAuthorized(Long customerId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.getPrincipal() instanceof CustomPrincipalDTO principalDTO) {
			if (principalDTO.getRole().equals(Role.ADMIN.name())) {
				return true;
			}
			if (principalDTO.getCustomerId().equals(customerId)) {
				return true;
			}
		}

		return false;
	}
}
