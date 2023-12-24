package com.quintor.worqplace.security;

import com.quintor.worqplace.security.filter.JwtAuthenticationFilter;
import com.quintor.worqplace.security.filter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * This class configures authentication and authorisation
 * for the application.
 * <p>
 * The configure method
 * - permits all POSTs to the registration and login endpoints
 * - requires all requests other URLs to be authenticated
 * - sets up JWT-based authentication and authorisation
 * - enforces sessions to be stateless (see: REST)
 * <p>
 * We make sure user data is securely stored
 * by utilizing a BcryptPasswordEncoder.
 * We don't store passwords, only hashes of passwords.
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Value("${uri.login-path}")
	public String loginPath;
	@Value("${uri.register-path}")
	public String registerPath;

	@Value("${security.jwt.secret}")
	private String jwtSecret;

	@Value("${security.jwt.expiration-in-ms}")
	private Integer jwtExpirationInMs;

	/**
	 * Configure authorization for endpoints.
	 *
	 * @param http {@link HttpSecurity}.
	 * @throws Exception when config is invalid.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and()
				.csrf().disable()
				.authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.antMatchers(HttpMethod.POST, registerPath).permitAll()
				.antMatchers(HttpMethod.POST, loginPath).permitAll()
				.anyRequest().authenticated()
				.and()
				.addFilterBefore(
						new JwtAuthenticationFilter(
								loginPath,
								this.jwtSecret,
								this.jwtExpirationInMs,
								this.authenticationManager()
						),
						UsernamePasswordAuthenticationFilter.class
				)
				.addFilter(new JwtAuthorizationFilter(this.jwtSecret, this.authenticationManager()))
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

	}

	/**
	 * @return {@link BCryptPasswordEncoder} used to encode passwords.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * This method needs instantiation in order for Spring Boot to generate a correct security chain.
	 * If this method isn't instantiated, CORS will fail due to incorrect chaining order.
	 */
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
		return source;
	}
}
