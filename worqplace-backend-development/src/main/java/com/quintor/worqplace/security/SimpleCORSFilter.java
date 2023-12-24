package com.quintor.worqplace.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * {@link SimpleCORSFilter} class that extends {@link Filter}.
 * Sets the required response headers for this application.
 */
@Component
public class SimpleCORSFilter implements Filter {

	/**
	 * Constructor of the class {@link SimpleCORSFilter}.
	 * <p>
	 * Will create the class and instantiate a logger with it.
	 */
	public SimpleCORSFilter() {
		Logger log = LoggerFactory.getLogger(SimpleCORSFilter.class);
		log.info("SimpleCORSFilter init");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");

		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig filterConfig) {
		// Can stay empty
	}

	@Override
	public void destroy() {
		// Can stay empty
	}
}
