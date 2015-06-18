package app.service.cognalys;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import app.commons.LockManager;
import app.domain.entities.User;
import app.security.TokenHandler;
import app.security.UserRole;
import app.service.redis.RedisService;
import app.service.user.UserService;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

@Service
public class CognalysServiceDefault implements CognalysService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static final String LOCKING_PREFIX = "lock#";
    private static final String UPGRADE_PREFIX = "upgrade#";
	
	private static final long SEVEN_DAYS = 1000 * 60 * 60 * 24 * 7;
	private static final Duration MAX_WAITING = Duration.of(100, ChronoUnit.MILLIS);
	
	@Value("${cognalys.id}")
	private String appId;
	
	@Value("${cognalys.token}")
	private String appToken;
	
	@Autowired
	private LockManager lock;
	@Autowired
	private UserService userService;
	@Autowired
	private RedisService redisService;
	@Autowired
	private StringRedisTemplate redis;
	@Autowired
	private TokenHandler tokenHandler;
	
	private String getLockUpgradeKey(String email) {
		return new StringBuilder()
				.append(LOCKING_PREFIX)
				.append(UPGRADE_PREFIX)
				.append(email)
			.toString();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public ResponseEntity<String> doPhoneCall(String phoneNumber, String email, String document) {
		String upgradeKey = getLockUpgradeKey(email);
		if (lock.obtainLock(upgradeKey, MAX_WAITING)) {
			try {
				HttpResponse<JsonNode> jsonNode = Unirest.get("https://www.cognalys.com/api/v1/otp")
			  			  .header("accept", "application/json")
			  			  .queryString("app_id", appId)
			  			  .queryString("access_token", appToken)
			  			  .queryString("mobile", phoneNumber)
		  			  .asJson();
				
				CognalysResponse cognalysResponse = new ObjectMapper().readValue(jsonNode.getRawBody(), CognalysResponse.class);
				if (cognalysResponse.status.equals("success")) {
					log.debug("Phone call made succesfully: " + cognalysResponse.mobile);
					
					JSONObject json = new JSONObject();
					json.put("document", document);
					json.put("keymatch", cognalysResponse.keymatch);
					json.put("mobile", cognalysResponse.mobile);
					
					this.redisService.flagUserUpgrade(email, json.toString(), true);
					return new ResponseEntity<String>(cognalysResponse.otp_start, HttpStatus.OK);
				} else {
					log.error("Cognalys responded with an error to a phone call request.");
					Set<Integer> keys = cognalysResponse.errors.keySet();
					for (Integer key : keys) {
						log.error("Code: " + key + ", Message: " + cognalysResponse.errors.get(key));
					}
					return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
				}
			} catch (Exception e) {
				log.error("Unexpected error upgrading user " + email, e);
				return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			} finally {
				lock.releaseLock(upgradeKey);
			}
		} else {
			log.error("We couldn't obtain upgrade lock for user " + email);
			return new ResponseEntity<String>(HttpStatus.REQUEST_TIMEOUT);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public ResponseEntity<String> doPhoneCallNumberVerification(String email, String verification) {
		String entry = redis.opsForValue().get(RedisService.USER_UPGRADE_PREFIX + email);
		if (entry != null) {
			JSONObject json;
			try {
				json = (JSONObject) new JSONParser().parse(entry);
			} catch (ParseException e) {
				log.error("Failed to parse UserUpgrade entity from redis: " + e.getMessage());
				return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			HttpResponse<JsonNode> jsonNode;
			try {
				jsonNode = Unirest.get("https://www.cognalys.com/api/v1/otp/confirm")
				  			  .header("accept", "application/json")
				  			  .queryString("app_id", appId)
				  			  .queryString("access_token", appToken)
				  			  .queryString("keymatch", json.get("keymatch"))
				  			  .queryString("otp", verification)
			  			  .asJson();
				
			} catch (Exception e) {
				log.error("Attempt to make Cognalys number verification failed: " + e.getMessage());
				return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			try {
				CognalysResponse cognalysResponse = new ObjectMapper().readValue(jsonNode.getRawBody(), CognalysResponse.class);
				if (cognalysResponse.status.equals("success")) {
					log.debug("Upgrade user verifications were successfull, upgrading user: " +  email);
					
					// upgrade user and update in database
					User mUser = this.userService.getUserByEmail(email);
					mUser.setDocument((String) json.get("document"));
					mUser.setPhone((String) json.get("mobile"));
					mUser.revokeRole(UserRole.GUEST);
					mUser.grantRole(UserRole.USER);
					this.userService.save(mUser);
					
					// remove flag from redis
					this.redisService.flagUserUpgrade(email, null, false);
					
					// create token with new permissions and send it back
					JSONObject jsonToken = new JSONObject();
					jsonToken.put("token", tokenHandler.createTokenForUser((User) SecurityContextHolder.getContext().getAuthentication().getDetails(), System.currentTimeMillis() + SEVEN_DAYS));
					return new ResponseEntity<String>(jsonToken.toString(), HttpStatus.OK);
				} else {
					log.error("Cognalys responded with an error to a number verification request.");
					Set<Integer> keys = cognalysResponse.errors.keySet();
					for (Integer key : keys) {
						log.error("Code: " + key + ", Message: " + cognalysResponse.errors.get(key));
					}
					return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
				}
			} catch (IOException e) {
				log.error("Cognalys response parsing failed on number verification phase: " + e.getMessage());
				return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		log.info("User upgrade verification number request but no upgrade process is pending for this user: " + email);
		return new ResponseEntity<String>(HttpStatus.PRECONDITION_FAILED);
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class CognalysResponse {
		
		@NotNull
		public String status;
		
		public String mobile;
		public String keymatch;
		public String otp_start;
		public JSONObject errors;
	}
}
