package app.service.redis;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

@Validated
public interface RedisService {
	
	// flags
	public final static String USER_VERIFICATION_EMAIL_PREFIX = "user#verification#email#";
	public final static String USER_CHANGES_SENSITIVE_PREFIX = "user#changes#sensitives#";
	
	void flagChangesSensitive(
			@NotNull(message = "{validate.redisService.flagSensitiveChanges.email}") String email,
			boolean setFlag);
	
	void flagVerificationEmail(
			@NotNull(message = "{validate.redisService.flagUserEmailVerification.email}") String email,
			String value,
			boolean setFlag);
}
