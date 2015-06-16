package app.service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import app.domain.entities.User;
import app.domain.repositories.UserRepository;
import app.security.UserRole;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserServiceDefault implements UserService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private final static String USER_RESET_FLAG_PREFIX = "user#reset#";
	private final static String USER_UNVERIFIED_PREFIX = "user#unverified#";
	private final static String USER_RESET_PASSWORD_PREFIX = "user#reset-password#";
	
	private final static long EXPIRATION_TIME_LONG = 24 * 60 * 1; // 1 day in minutes
	private final static long EXPIRATION_TIME_RESET_FLAG_LONG = 10; // minutes to reset password when flagged
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RedisTemplate<String, String> redis;

	@Override
	public User getUser(Integer id) {
		return this.userRepository.findOne(id);
	}
	
	@Override
	public User getUserByEmail(String email) {
		return this.userRepository.findByEmail(email);
	}
	
	@Override
	public User getUserByPhone(String phone) {
		return this.userRepository.findByPhone(phone);
	}
	
	@Override
	public User getUserByDocument(String document) {
		return this.userRepository.findByDocument(document);
	}

	@Override
	public User save(User user) {
		return this.userRepository.save(user);
	}

	@Override
	public void deleteUser(Integer id) {
		this.userRepository.delete(id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean saveUnverifiedUser(User aUser, String secret) throws JsonProcessingException {
		if (redis.opsForValue().get(USER_UNVERIFIED_PREFIX + aUser.getEmail()) == null) {
			JSONObject json = new JSONObject();
			json.put("user", new ObjectMapper().writeValueAsString(aUser));
			json.put("password", new BCryptPasswordEncoder().encode(aUser.getPassword()));
			json.put("secret", secret);
			
			redis.opsForValue().set(USER_UNVERIFIED_PREFIX + aUser.getEmail(), json.toString());
			redis.expire(USER_UNVERIFIED_PREFIX + aUser.getEmail(), EXPIRATION_TIME_LONG, TimeUnit.MINUTES);
			
			return true;
		}
		return false;
	}

	@Override
	public boolean verifyUser(String email, String secret) throws ParseException {
		if (email != null && secret != null) {
			String entry = redis.opsForValue().get(USER_UNVERIFIED_PREFIX + email);
			if (entry != null) {
				JSONObject json = (JSONObject) new JSONParser().parse(entry);
				if (secret.equals(json.get("secret"))) {
					try {
						User mUser = new ObjectMapper().configure(Feature.IGNORE_UNKNOWN, true).readValue(json.get("user").toString(), User.class);
						Set<UserRole> roles = new HashSet<>();
						roles.add(UserRole.GUEST);
						mUser.setRoles(roles);
						mUser.setPassword(json.get("password").toString());
						
						// save new user to database
						save(mUser);
					} catch (Exception e) {
						log.error("User parsing from redis record failed: " + e.getMessage());
						return false;
					} finally {
						// remove from redis
						redis.delete(USER_UNVERIFIED_PREFIX + email);
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveResetPasswordUser(User aUser, String secret) throws JsonProcessingException {
		JSONObject json = new JSONObject();
		json.put("user", new ObjectMapper().writeValueAsString(aUser));
		json.put("password", new BCryptPasswordEncoder().encode(aUser.getPassword()));
		json.put("secret", secret);
		
		redis.opsForValue().set(USER_RESET_PASSWORD_PREFIX + aUser.getEmail(), json.toString());
		redis.expire(USER_RESET_PASSWORD_PREFIX + aUser.getEmail(), EXPIRATION_TIME_LONG, TimeUnit.MINUTES);
	}

	@Override
	public boolean verifyUserReset(String email, String secret) throws ParseException {
		if (email != null && secret != null) {
			String entry = redis.opsForValue().get(USER_RESET_PASSWORD_PREFIX + email);
			if (entry != null) {
				JSONObject json = (JSONObject) new JSONParser().parse(entry);
				if (secret.equals(json.get("secret"))) {
					// remove from redis
					redis.delete(USER_RESET_PASSWORD_PREFIX + email);
					
					// flag user as able to reset password for the next 10 minutes
					redis.opsForValue().set(USER_RESET_FLAG_PREFIX + email, "");
					redis.expire(USER_RESET_FLAG_PREFIX + email, EXPIRATION_TIME_RESET_FLAG_LONG, TimeUnit.MINUTES);
					return true;
				}
			}
		}
		return false;
	}
}
