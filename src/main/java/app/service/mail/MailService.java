package app.service.mail;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

@Validated
public interface MailService {
	
	void sendSignUpEmail(
			@NotNull( message = "{validate.mailService.sendSignUpEmail.emailTo}") String emailTo, 
			@NotNull( message = "{validate.userService.sendSignUpEmail.secret}") String secret, 
			@NotNull( message = "{validate.userService.sendSignUpEmail.request}") HttpServletRequest request);
	
	void sendResetPasswordEmail(
			@NotNull( message = "{validate.mailService.sendResetEmail.emailTo}") String emailTo, 
			@NotNull( message = "{validate.mailService.sendResetEmail.secret}") String secret, 
			@NotNull( message = "{validate.mailService.sendResetEmail.request}") HttpServletRequest request);
}
