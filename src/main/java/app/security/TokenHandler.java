package app.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import app.domain.entities.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public final class TokenHandler {
	
	@Value("${token.issuer}")
	private String issuer;

	public User parseUserFromToken(String token) {
		PublicKey key = KeyReader.getPublicKey("keys/public-key.der");
		User user = null;
		
		try {
			assert Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getIssuer().equals(issuer);
			String json = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
			System.out.println(json);
			user = new ObjectMapper().readValue(Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject(), User.class);
		} catch (AssertionError | ExpiredJwtException | UnsupportedJwtException
				| MalformedJwtException | SignatureException
				| IllegalArgumentException | IOException e) {
			Logger logger = LogManager.getLogger(TokenHandler.class.getName());
			logger.error("Tampering attempt detected. Error: " + e.getStackTrace() + ", Token: " + token);
		}
		
		try {
			assert Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getExpiration().after(new Date());
			return user;
		} catch (AssertionError error) {
			Logger logger = LogManager.getLogger(TokenHandler.class.getName());
			logger.debug("Token expiration. Token: " + token);
		}
		return null;
	}

	public String createTokenForUser(User user, long expires) {
		PrivateKey privateKey = KeyReader.getPrivateKey("keys/private-key.der");
		
		try {
			return Jwts.builder().setSubject(new ObjectMapper().writeValueAsString(user)).
					setExpiration(new Date(expires)).
					setIssuer(issuer).
					signWith(SignatureAlgorithm.RS512, privateKey).compact();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			Logger logger = LogManager.getLogger(TokenHandler.class.getName());
			logger.error("User serialization failed. Error: " + e.getStackTrace() + ", User: " + user.toString());
		}
		return null;
	}
}
