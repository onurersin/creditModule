package com.creditModule.challenge.filter;

import com.creditModule.challenge.constant.Constants;
import com.creditModule.challenge.dto.CustomPrincipalDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	private static final String BEARER = "Bearer ";
	@Value("${jwt.token.secret}")
	private String SECRET_KEY;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain) throws IOException, ServletException {

		String authHeader = request.getHeader(Constants.HEADER_AUTHORIZATION);
		if (authHeader != null && authHeader.startsWith(BEARER)) {
			String jwt = authHeader.substring(BEARER.length());

			try {
				Claims claims = Jwts.parser()
						.setSigningKey(SECRET_KEY)
						.parseClaimsJws(jwt)
						.getBody();

				Long userId = claims.get(Constants.CLAIM_USER_ID, Long.class);
				Long customerId = claims.get(Constants.CLAIM_CUSTOMER_ID, Long.class);
				String role = claims.get(Constants.CLAIM_ROLE, String.class);

				CustomPrincipalDTO customPrincipal = new CustomPrincipalDTO(userId, customerId, role);


				UsernamePasswordAuthenticationToken authenticationToken
						=
						new UsernamePasswordAuthenticationToken(
								customPrincipal,
								null, // no credentials
								Collections.singleton(() -> "ROLE_" + role)
						);

				SecurityContextHolder.getContext().setAuthentication(authenticationToken);

			} catch (Exception e) {
				SecurityContextHolder.clearContext();
			}
		}

		filterChain.doFilter(request, response);
	}
}
