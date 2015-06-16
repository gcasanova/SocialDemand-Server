package app.security.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import app.domain.entities.User;
import app.security.TokenHandler;
import app.security.UserAuthentication;

@Service
public class TokenAuthenticationService {
	
	private static final String AUTH_HEADER_NAME = "Authorization";
	private static final long SEVEN_DAYS = 1000 * 60 * 60 * 24 * 7;
	
	@Autowired
	private TokenHandler tokenHandler;

	@SuppressWarnings("unchecked")
	public void addAuthentication(HttpServletResponse response, UserAuthentication authentication) throws IOException {
		final User user = authentication.getDetails();
		
		JSONObject json = new JSONObject();
		json.put("token", tokenHandler.createTokenForUser(user, System.currentTimeMillis() + SEVEN_DAYS));
		response.getWriter().write(json.toString());
	}

	public Authentication getAuthentication(HttpServletRequest request) {
		final String token = request.getHeader(AUTH_HEADER_NAME);
		if (token != null) {
			String[] tokenParts = token.split(" ");
			final User user = tokenHandler.parseUserFromToken(tokenParts.length > 1 ? tokenParts[tokenParts.length - 1] : token); // ignore 'Bearer' string if present
			if (user != null) {
				return new UserAuthentication(user);
			}
		}
		return null;
	}
}
