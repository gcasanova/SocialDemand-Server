package app.web;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.domain.entities.User;
import app.domain.entities.location.Location;
import app.service.MailService;
import app.service.UserService;

@RestController
@RequestMapping("/api/user")
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
	public ResponseEntity<User> read(@PathVariable("id") Integer id) {
		User mUser = this.userService.getUser(id);
		if (mUser == null)
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		
		return new ResponseEntity<User>(mUser, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}/location", method = RequestMethod.GET)
	public ResponseEntity<User> readWithLocation(@PathVariable("id") Integer id) {
		User mUser = this.userService.getUser(id);
		if (mUser == null)
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		
		mUser.setLocation(location.build(mUser.getMunicipalityId()));
		return new ResponseEntity<User>(mUser, HttpStatus.OK);
	}
}
