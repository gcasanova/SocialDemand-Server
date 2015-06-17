package app.service.user;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.json.simple.parser.ParseException;
import org.springframework.validation.annotation.Validated;

import app.domain.entities.User;

import com.fasterxml.jackson.core.JsonProcessingException;

@Validated
public interface UserService {

	User getUser(
			@NotNull(message = "{validate.userService.getUser.id}") Integer id);
	
	User getUserByEmail(
			@NotNull(message = "{validate.userService.getUserByEmail.email}") String email);
	
	User getUserByPhone(
			@NotNull(message = "{validate.userService.getUserByPhone.phone}") String phone);
	
	User getUserByDocument(
			@NotNull(message = "{validate.userService.getUserByDocument.document}") String document);

	User save(
			@NotNull(message = "{validate.userService.save.user}") @Valid User user);
	
	boolean setVerificationEmailFlag(
			@NotNull(message = "{validate.userService.saveUnverifiedUser.user}") @Valid User aUser,
			@NotNull(message = "{validate.userService.saveUnverifiedUser.secret}") String secret) throws JsonProcessingException;
	
	boolean verifySignUpVerificationEmail(
			@NotNull(message = "{validate.userService.verifyUser.email}") String email,
			@NotNull(message = "{validate.userService.verifyUser.secret}") String secret) throws ParseException;
	
	boolean verifyPasswordResetVerificationEmail(
			@NotNull(message = "{validate.userService.verifyUserReset.email}") String email,
			@NotNull(message = "{validate.userService.verifyUserReset.secret}") String secret) throws ParseException;

	void deleteUser(Integer id);
}
