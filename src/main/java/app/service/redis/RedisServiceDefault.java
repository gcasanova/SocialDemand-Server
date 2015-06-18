package app.service.redis;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceDefault implements RedisService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	// expiration times
	private final static long EXPIRATION_TIME_24_HOURS_MILLIS = 24 * 60 * 60 * 1000;
	private final static long EXPIRATION_TIME_10_MINUTES_MILLIS = 10 * 60 * 1000;
	
	@Autowired
	private StringRedisTemplate redis;

	@Override
	public void flagChangesSensitive(String email, boolean setFlag) {
		if (setFlag) {
			
			log.debug("Setting sensitive data changes flag on redis for user: " + email);
			try {
				redis.opsForValue().set(USER_CHANGES_SENSITIVE_PREFIX + email, "");
				redis.expire(USER_CHANGES_SENSITIVE_PREFIX + email, EXPIRATION_TIME_10_MINUTES_MILLIS, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				log.error("Sensitive data changes flag FAILED: " + e.getMessage());
				
				// in case the first operation went through and second failed, try to delete it
				redis.delete(USER_CHANGES_SENSITIVE_PREFIX + email);
			}
		} else {
			log.debug("Removing sensitive data changes flag from redis for user: " + email);
			redis.delete(USER_CHANGES_SENSITIVE_PREFIX + email);
		}
	}

	@Override
	public void flagVerificationEmail(String email, String value, boolean setFlag) {
		if (setFlag) {
			assert value != null;
			
			log.debug("Setting email verification flag on redis for user: " + email);
			try {
				redis.opsForValue().set(USER_VERIFICATION_EMAIL_PREFIX + email, value);
				redis.expire(USER_VERIFICATION_EMAIL_PREFIX + email, EXPIRATION_TIME_24_HOURS_MILLIS, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				log.error("Email verification flag FAILED: " + e.getMessage());
				
				// in case the first operation went through and second failed, try to delete it
				redis.delete(USER_VERIFICATION_EMAIL_PREFIX + email);
			}
		} else {
			log.debug("Removing email verification flag from redis for user: " + email);
			redis.delete(USER_VERIFICATION_EMAIL_PREFIX + email);
		}
	}

	@Override
	public void flagUserUpgrade(String email, String value, boolean setFlag) {
		if (setFlag) {
			assert value != null;
			
			log.debug("Setting user upgrade flag on redis for user: " + email);
			try {
				redis.opsForValue().set(USER_UPGRADE_PREFIX + email, value);
				redis.expire(USER_UPGRADE_PREFIX + email, EXPIRATION_TIME_10_MINUTES_MILLIS, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				log.error("User upgrade flag FAILED: " + e.getMessage());
				
				// in case the first operation went through and second failed, try to delete it
				redis.delete(USER_UPGRADE_PREFIX + email);
			}
		} else {
			log.debug("Removing user upgrade flag from redis for user: " + email);
			redis.delete(USER_UPGRADE_PREFIX + email);
		}
	}
}
