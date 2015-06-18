package app.service.cognalys;

import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CognalysService {
	
	ResponseEntity<String> doPhoneCall(
			@NotNull(message = "{validate.cognalysService.doPhoneCall.phoneNumber}") String phoneNumber,
			@NotNull(message = "{validate.cognalysService.doPhoneCall.email}") String email,
			@NotNull(message = "{validate.cognalysService.doPhoneCall.document}") String document);
	
	ResponseEntity<String> doPhoneCallNumberVerification(
			@NotNull(message = "{validate.cognalysService.doPhoneCallNumberVerification.email}") String email,
			@NotNull(message = "{validate.cognalysService.doPhoneCallNumberVerification.verification}") String verification);
}
