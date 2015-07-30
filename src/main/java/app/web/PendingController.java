package app.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.service.redis.RedisService;
import app.service.user.UserService;

@RestController
@RequestMapping("/api/pending")
public class PendingController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private RedisService redisService;
	@Autowired
	private StringRedisTemplate redis;
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ResponseEntity<String> read(@RequestParam("email") String email) {
		// check verification email error
		String verified = this.redis.opsForValue().get(RedisService.USER_VERIFIED_EMAIL_PREFIX + email);
		if (verified != null && Boolean.parseBoolean(verified) == false) {
			this.redisService.flagVerifiedEmail(email, null, false);
			return new ResponseEntity<String>("Email verification link was expired, please sign up again", HttpStatus.OK);
		}
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ResponseEntity<String> readLogin(@RequestParam("email") String email) {
		// check verified email
		String verified = this.redis.opsForValue().get(RedisService.USER_VERIFIED_EMAIL_PREFIX + email);
		if (verified != null && Boolean.parseBoolean(verified) == true) {
			this.redisService.flagVerifiedEmail(email, null, false);
			return new ResponseEntity<String>("Email verified, please login now", HttpStatus.OK);
		}
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}
}
