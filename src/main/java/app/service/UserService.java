package app.service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.json.simple.parser.ParseException;
import org.springframework.validation.annotation.Validated;

import app.domain.entities.User;

import com.fasterxml.jackson.core.JsonProcessingException;

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
	
	boolean saveUnverifiedUser(
			@NotNull(message = "{validate.userService.saveUnverifiedUser.user}") @Valid User aUser,
			@NotNull(message = "{validate.userService.saveUnverifiedUser.secret}") String secret) throws JsonProcessingException;
	
	void saveResetPasswordUser(
			@NotNull(message = "{validate.userService.saveResetPasswordUser.user}") @Valid User aUser,
			@NotNull(message = "{validate.userService.saveResetPasswordUser.secret}") String secret) throws JsonProcessingException;
	
	boolean verifyUser(
			@NotNull(message = "{validate.userService.save.user}") String email,
			@NotNull(message = "{validate.userService.save.user}") String secret) throws ParseException;
	
	boolean verifyUserReset(
			@NotNull(message = "{validate.userService.save.user}") String email,
			@NotNull(message = "{validate.userService.save.user}") String secret) throws ParseException;

	void deleteUser(Integer id);
}
