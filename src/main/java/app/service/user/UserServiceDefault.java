package app.service.user;

import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import app.domain.entities.User;
import app.domain.repositories.UserRepository;
import app.security.UserRole;
import app.service.redis.RedisService;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserServiceDefault implements UserService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private StringRedisTemplate redis;
	@Autowired
	private RedisService redisService;
	@Autowired
	private UserRepository userRepository;

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
	public boolean setVerificationEmailFlag(User aUser, String secret) throws JsonProcessingException {
		if (redis.opsForValue().get(RedisService.USER_VERIFICATION_EMAIL_PREFIX + aUser.getEmail()) == null) {
			JSONObject json = new JSONObject();
			json.put("user", new ObjectMapper().writeValueAsString(aUser));
			json.put("password", new BCryptPasswordEncoder().encode(aUser.getPassword()));
			json.put("secret", secret);
			
			this.redisService.flagVerificationEmail(aUser.getEmail(), json.toString(), true);
			return true;
		}
		return false;
	}

	@Override
	public boolean verifySignUpVerificationEmail(String email, String secret) throws ParseException {
		if (email != null && secret != null) {
			String entry = redis.opsForValue().get(RedisService.USER_VERIFICATION_EMAIL_PREFIX + email);
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
						this.redisService.flagVerificationEmail(email, null, false);
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean verifyPasswordResetVerificationEmail(String email, String secret) throws ParseException {
		if (email != null && secret != null) {
			String entry = redis.opsForValue().get(RedisService.USER_VERIFICATION_EMAIL_PREFIX + email);
			if (entry != null) {
				JSONObject json = (JSONObject) new JSONParser().parse(entry);
				if (secret.equals(json.get("secret"))) {
					// remove from redis
					this.redisService.flagVerificationEmail(email, null, false);
					
					// flag user as able to reset password for the next 10 minutes
					this.redisService.flagChangesSensitive(email, true);
					return true;
				}
			}
		}
		return false;
	}
}
