package com.quintor.worqplace.security.filter;


import com.quintor.worqplace.security.data.UserProfile;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Tries to authorize a user, based on the Bearer token (JWT) from
 * the Authorization header of the incoming request.
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
	private final String secret;

	/**
	 * Constructor of the {@link JwtAuthorizationFilter} class that extends {@link BasicAuthenticationFilter}.
	 *
	 * @param secret                Secret.
	 * @param authenticationManager {@link AuthenticationManager} object.
	 */
	public JwtAuthorizationFilter(String secret, AuthenticationManager authenticationManager) {
		super(authenticationManager);
		this.secret = secret;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
	                                FilterChain filterChain) throws IOException, ServletException {

		Authentication authentication = this.getAuthentication(request);

		if (authentication != null)
			SecurityContextHolder.getContext().setAuthentication(authentication);

		filterChain.doFilter(request, response);
	}

	/**
	 * Authentication getter that verifies the token, creates a key and parses the JWT.
	 * Then creates an {@link UsernamePasswordAuthenticationToken} with the verified and given credentials.
	 *
	 * @param request {@link HttpServletRequest}.
	 * @return {@link Authentication} object.
	 */
	private Authentication getAuthentication(HttpServletRequest request) {
		String token = request.getHeader("Authorization");

		if (token == null || token.isEmpty() || !token.startsWith("Bearer "))
			return null;

		byte[] signingKey = this.secret.getBytes();

		JwtParser jwtParser = Jwts.parserBuilder()
				.setSigningKey(signingKey)
				.build();

		Jws<Claims> parsedToken = jwtParser
				.parseClaimsJws(token.replace("Bearer ", ""));

		var username = parsedToken
				.getBody()
				.getSubject();

		var authorities = ((List<?>) parsedToken.getBody().get("role"))
				.stream()
				.map(authority -> new SimpleGrantedAuthority((String) authority))
				.toList();

		if (username.isEmpty())
			return null;

		UserProfile principal = new UserProfile(username);

		return new UsernamePasswordAuthenticationToken(principal, null, authorities);
	}
}
