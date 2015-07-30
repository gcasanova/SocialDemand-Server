package app.web;

import java.security.SecureRandom;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.domain.entities.User;
import app.domain.entities.location.Location;
import app.service.mail.MailService;
import app.service.user.UserService;

@RestController
@RequestMapping("/api/users")
@SuppressWarnings("unchecked")
public class UserController {

	@Autowired
	private Location location;
	@Autowired
	private SecureRandom random;
	@Autowired
	private UserService userService;
	@Autowired
	private MailService mailService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> read(@PathVariable("id") Integer id) {
		User mUser = this.userService.getUser(id);
		if (mUser == null)
			return new ResponseEntity<JSONObject>(HttpStatus.NOT_FOUND);
		
		JSONObject json = new JSONObject();
		json.put("user", mUser);
		return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
	}
}
