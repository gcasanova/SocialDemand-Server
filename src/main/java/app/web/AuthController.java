package app.web;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import app.domain.entities.User;
import app.security.TokenHandler;
import app.service.cognalys.CognalysService;
import app.service.mail.MailService;
import app.service.redis.RedisService;
import app.service.user.UserService;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static final long SEVEN_DAYS = 1000 * 60 * 60 * 24 * 7;
	
	@Value("${aws.host.public.dns.client}")
	private String hostname;
	
	@Value("${aws.host.protocol.client}")
	private String protocol;
	
	@Autowired
	private SecureRandom random;
	@Autowired
	private UserService userService;
	@Autowired
	private MailService mailService;
	@Autowired
	private TokenHandler tokenHandler;
	@Autowired
	private RedisService redisService;
	@Autowired
	private StringRedisTemplate redis;
	@Autowired
	private CognalysService cognalysService;
	@Autowired
	private AuthenticationManager authenticationManager;
	
	/*
	 * Refresh valid token
	*/
	@SuppressWarnings("unchecked")
	@ResponseStatus(HttpStatus.ACCEPTED)
	@RequestMapping(value = "/refresh", method = RequestMethod.POST)
	public void refresh(HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		json.put("token", tokenHandler.createTokenForUser((User) SecurityContextHolder.getContext().getAuthentication().getDetails(), System.currentTimeMillis() + SEVEN_DAYS));
		response.getWriter().write(json.toString());
	}
	
	/*
	 * Entry point for new registrations to the system
	*/
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public ResponseEntity<?> signup(HttpServletRequest request, @RequestBody @Valid User aUser) {
		if (this.userService.getUserByEmail(aUser.getEmail()) == null) {
			try {
				String secret = new BigInteger(130, random).toString(32);
				
				if (this.userService.setVerificationEmailFlag(aUser, secret)) {
					this.mailService.sendSignUpEmail(aUser.getEmail(), secret, request);
					return new ResponseEntity<String>(HttpStatus.OK);
				}
				
				// user submitted sign up data a couple of minutes ago
				return new ResponseEntity<String>("Email confirmation was already sent, check your email inbox", HttpStatus.PRECONDITION_FAILED);
			} catch (JsonProcessingException | ParseException e) {
				log.error("Error parsing on sign: " + e.getMessage());
			}
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// user with this email already exists
		return new ResponseEntity<String>("Email already registered for other user", HttpStatus.FORBIDDEN);
	}
	
	/*
	 * Endpoint reached by the user when the link of the verification email is clicked (for new registrations)
	*/
	@RequestMapping(value = "/signup/verification", method = RequestMethod.GET)
	public ResponseEntity<?> signupVerificationEmail(HttpServletResponse response, @RequestParam("email") String email, @RequestParam("token") String token) {
		try {
			if (this.userService.verifySignUpVerificationEmail(email, token)) {
				try {
					response.sendRedirect(protocol + "://" + hostname);
				} catch (IOException e) {
					log.error("Redirect to /login route after signup verification failed: " + e.getMessage(), e.getCause());
				}
				return new ResponseEntity<String>(HttpStatus.OK);
			}
		} catch (ParseException e) {
			log.error("Error parsing on verifyEmail: " + e.getMessage());
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		try {
			response.sendRedirect(protocol + "://" + hostname + "/invalid");
		} catch (IOException e) {
			log.error("Redirect to /invalid route after signup verification failed: " + e.getMessage(), e.getCause());
		}
		return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
	}
	
	/*
	 * Entry point for when the user has forgotten the password
	*/
	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	public ResponseEntity<?> resetPassword(HttpServletRequest request, @RequestParam("email") String email) {
		User mUser = this.userService.getUserByEmail(email);
		if (mUser != null) {
			try {
				String secret = new BigInteger(130, random).toString(32);
				this.userService.setVerificationEmailFlag(mUser, secret);
				this.mailService.sendResetPasswordEmail(mUser.getEmail(), secret, request);
				return new ResponseEntity<String>(HttpStatus.OK);
			} catch (JsonProcessingException | ParseException e) {
				log.error("Error parsing on sign: " + e.getMessage());
			}
		}
		return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
	}
	
	/*
	 * Endpoint reached by the user when the link of the verification email is clicked (for password reset requests)
	*/
	@RequestMapping(value = "/reset/verification", method = RequestMethod.GET)
	public ResponseEntity<?> resetVerificationEmail(HttpServletResponse response, @RequestParam("email") String email, @RequestParam("token") String token) {
		try {
			if (this.userService.verifyPasswordResetVerificationEmail(email, token)) {
				try {
					response.sendRedirect(protocol + "://" + hostname);
				} catch (IOException e) {
					log.error("Redirect to /reset route after forgotten verification failed: " + e.getMessage(), e.getCause());
				}
				return new ResponseEntity<String>(HttpStatus.OK);
			}
		} catch (ParseException e) {
			log.error("Error parsing on verifyEmail: " + e.getMessage());
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		try {
			response.sendRedirect(protocol + "://" + hostname + "/invalid");
		} catch (IOException e) {
			log.error("Redirect to /invalid route after forgotten verification failed: " + e.getMessage(), e.getCause());
		}
		return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
	}
	
	/*
	 * Double check user credentials and allow sensitive data changes for this user for a given period of time
	*/
	@RequestMapping(value = "/check", method = RequestMethod.POST)
	public ResponseEntity<?> doubleCheckCredentials(@RequestHeader("Email") String email, @RequestHeader("Password") String password) {        
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        
		if (authenticationManager.authenticate(token) != null) {
			this.redisService.flagChangesSensitive(email, true);
			return new ResponseEntity<String>(HttpStatus.OK);
		}
		return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
	}
	
	/*
	 * Endpoint to change the password, prior verification must have been done for this to be successful, this endpoint 
	 * is not secured because it is also used for users that have forgotten their password and do not have a valid token
	*/
	@RequestMapping(value = "/change", method = RequestMethod.POST)
	public ResponseEntity<?> changePassword(@RequestHeader("Email") String email, @RequestHeader("Password") String password) {
		if (this.redis.opsForValue().get(RedisService.USER_CHANGES_SENSITIVE_PREFIX + email) != null) {
			try {
				User mUser = this.userService.getUserByEmail(email);
				
				if (mUser != null) {
					mUser.setPassword(new BCryptPasswordEncoder().encode(password));
					this.userService.save(mUser);
					
					log.debug("Password changed for user : " + email);
					return new ResponseEntity<String>(HttpStatus.OK);
				} else {
					log.info("Password change request with an email not registered. Email: " + email);
					return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
				}
			} catch (Exception e) {
				log.error("Error changing password : " + e.getMessage());
				return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			} finally {
				this.redisService.flagChangesSensitive(email, false);
			}
		}
		log.info("Password change request made without having confirmed email first. Email: " + email);
		return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
	}
	
	/*
	 * Phone number verification via Cognalys phase 1
	*/
	@RequestMapping(value = "/upgrade", method = RequestMethod.POST)
	public ResponseEntity<?> upgrade(@RequestHeader("Document") String document, @RequestHeader("Mobile") String mobile) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		
		assert email != null;
		if (this.userService.getUserByDocument(document) == null && this.userService.getUserByPhone(mobile) == null) {
			if (this.redis.opsForValue().get(RedisService.USER_UPGRADE_PREFIX + email) == null) {
				return this.cognalysService.doPhoneCall(mobile, email, document);
			}
		}
		log.info("User upgrade while upgrade verification number is pending yet: " + email);
		return new ResponseEntity<String>(HttpStatus.PRECONDITION_FAILED);
	}
	
	/*
	 * Phone number verification via Cognalys phase 2
	*/
	@RequestMapping(value = "/upgrade/verification", method = RequestMethod.POST)
	public ResponseEntity<?> upgradeVerification(@RequestHeader("Verification") String verification) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		
		assert email != null;
		return this.cognalysService.doPhoneCallNumberVerification(email, verification);
	}
}
