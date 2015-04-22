package app.service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import app.domain.entities.User;

@Validated
public interface UserService {

	User getUser(
			@NotNull( message = "{validate.userService.getUser.id}") Integer id);
	
	User getUserByEmail(
			@NotNull(message = "{validate.userService.getUserByEmail.email}") String email);
	
	User getUserByPhone(
			@NotNull(message = "{validate.userService.getUserByPhone.phone}") String phone);
	
	User getUserByDocument(
			@NotNull(message = "{validate.userService.getUserByDocument.document}") String document);

	User save(
			@NotNull(message = "{validate.userService.save.user}") @Valid User user);

	void deleteUser(Integer id);
}
