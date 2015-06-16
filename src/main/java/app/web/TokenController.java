package app.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import app.domain.entities.User;
import app.security.TokenHandler;

@RestController
@RequestMapping("/api/token")
public class TokenController {
	
	private static final long SEVEN_DAYS = 1000 * 60 * 60 * 24 * 7;
	
	@Autowired
	private TokenHandler tokenHandler;

	@SuppressWarnings("unchecked")
	@ResponseStatus(HttpStatus.ACCEPTED)
	@RequestMapping(value = "/refresh", method = RequestMethod.POST)
	public void refresh(HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		json.put("token", tokenHandler.createTokenForUser((User) SecurityContextHolder.getContext().getAuthentication().getDetails(), System.currentTimeMillis() + SEVEN_DAYS));
		response.getWriter().write(json.toString());
	}
}
