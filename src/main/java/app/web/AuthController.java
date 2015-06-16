package app.web;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.domain.entities.User;
import app.service.MailService;
import app.service.UserService;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SecureRandom random;
	@Autowired
	private UserService userService;
	@Autowired
	private MailService mailService;
	
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public ResponseEntity<User> register(HttpServletRequest request, @RequestBody @Valid User aUser) {
		try {
			String secret = new BigInteger(130, random).toString(32);
			
			if (this.userService.saveUnverifiedUser(aUser, secret)) {
				this.mailService.sendVerificationEmail(aUser.getEmail(), secret, request);
				return new ResponseEntity<User>(HttpStatus.OK);
			}
			
			// user already in redis (should request link resend instead)
			return new ResponseEntity<User>(HttpStatus.PRECONDITION_FAILED);
		} catch (JsonProcessingException e) {
			log.error("Error parsing on sign: " + e.getMessage());
		}
		return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@RequestMapping(value = "/verification", method = RequestMethod.GET)
	public ResponseEntity<User> verifyEmail(@RequestParam("email") String email, @RequestParam("token") String token) {
		try {
			if (this.userService.verifyUser(email, token)) {
				// redirect to login page (to be implemented)
				return new ResponseEntity<User>(HttpStatus.OK);
			}
		} catch (ParseException e) {
			log.error("Error parsing on verifyEmail: " + e.getMessage());
			return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<User>(HttpStatus.FORBIDDEN);
	}
	
	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	public ResponseEntity<User> reset(HttpServletRequest request, @RequestParam("email") String email) {
		User mUser = this.userService.getUserByEmail(email);
		if (mUser != null) {
			try {
				String secret = new BigInteger(130, random).toString(32);
				this.userService.saveResetPasswordUser(mUser, secret);
				this.mailService.sendPasswordResetEmail(mUser.getEmail(), secret, request);
				return new ResponseEntity<User>(HttpStatus.OK);
			} catch (JsonProcessingException e) {
				log.error("Error parsing on sign: " + e.getMessage());
			}
		}
		return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
	public ResponseEntity<User> verifyResetPassword(@RequestParam("email") String email, @RequestParam("token") String token) {
		try {
			if (this.userService.verifyUserReset(email, token)) {
				// redirect to password reset page (to be implemented)
				return new ResponseEntity<User>(HttpStatus.OK);
			}
		} catch (ParseException e) {
			log.error("Error parsing on verifyEmail: " + e.getMessage());
			return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<User>(HttpStatus.FORBIDDEN);
	}
}
