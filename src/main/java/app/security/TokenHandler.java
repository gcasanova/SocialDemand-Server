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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import app.domain.entities.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public final class TokenHandler {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Value("${token.issuer}")
	private String issuer;
	
	@Autowired
	private KeyReader keyReader;

	public User parseUserFromToken(String token) {
		PublicKey key = keyReader.getPublicKey("keys/public-key.der");
		User user = null;
		
		try {
			assert Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getIssuer().equals(issuer);
			user = new ObjectMapper().readValue(Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject(), User.class);
		} catch (AssertionError | ExpiredJwtException | UnsupportedJwtException
				| MalformedJwtException | SignatureException
				| IllegalArgumentException | IOException e) {
			
			log.error("Tampering attempt detected. Error: " + e.getMessage() + ", Token: " + token);
			return null;
		}
		
		try {
			assert Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getExpiration().after(new Date());
			return user;
		} catch (AssertionError error) {
			log.debug("Token expiration. Token: " + token);
		}
		return null;
	}

	public String createTokenForUser(User user, long expires) {
		PrivateKey privateKey = keyReader.getPrivateKey("keys/private-key.der");
		
		try {
			return Jwts.builder().setSubject(new ObjectMapper().writeValueAsString(user))
							.setExpiration(new Date(expires))
							.setIssuer(issuer)
							.signWith(SignatureAlgorithm.RS512, privateKey)
						.compact();
		} catch (JsonProcessingException e) {
			log.error("User serialization failed. Error: " + e.getMessage() + ", User: " + user.toString());
		}
		return null;
	}
}
