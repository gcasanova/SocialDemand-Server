package app.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.domain.entities.User;
import app.domain.entities.location.Location;
import app.exceptions.ResourceNotFoundException;
import app.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private Location location;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public User read(@PathVariable("id") Integer id) {
		User aUser = this.userService.getUser(id);
		if (aUser == null)
			throw new ResourceNotFoundException();
		return aUser;
	}
	
	@RequestMapping(value = "/{id}/location", method = RequestMethod.GET)
	public User readWithLocation(@PathVariable("id") Integer id) {
		User aUser = this.userService.getUser(id);
		if (aUser == null)
			throw new ResourceNotFoundException();
		
		aUser.setLocation(location.build(aUser.getMunicipalityId()));
		return aUser;
	}
}
