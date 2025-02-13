package com.creditModule.challenge.util;

import com.creditModule.challenge.constant.Constants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

	@Value("${jwt.token.secret}")
	private String SECRET_KEY;

	public String generateToken(Long userId, Long customerId, String role) {

		Map<String, Object> claims = new HashMap<>();
		claims.put(Constants.CLAIM_USER_ID, userId);
		claims.put(Constants.CLAIM_CUSTOMER_ID, customerId);
		claims.put(Constants.CLAIM_ROLE, role);

		long nowMillis = System.currentTimeMillis();
		long expirationMillis = nowMillis + (60 * 60 * 1000 * 24); // 24 hours

		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(new Date(nowMillis))
				.setExpiration(new Date(expirationMillis))
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY)
				.compact();
	}
}
