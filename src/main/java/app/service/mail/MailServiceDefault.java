package app.service.mail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailServiceDefault implements MailService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Value("${email.name}")
	private String email;
	
	@Value("${email.password}")
	private String password;
	
	@Value("${aws.host.public.dns}")
	private String hostname;
	
	@Value("${aws.host.port}")
	private String port;
	
	@Value("${aws.host.protocol}")
	private String protocol;

	@Async
	@Override
	public void sendSignUpEmail(String emailTo, String secret, HttpServletRequest request) {
		String to = emailTo;
		String subject = "Social Demand - Email Verification Link";
		String html = "Please click on the following link to verify your account, \n\n<a href='" + protocol + "://" + hostname + ":" + port + "/api/auth/signup/verification?email=" + emailTo + "&token=" + secret + "'>VERIFICATION LINK!</a>";

		Properties props = new Properties();
		props.setProperty("mail.host", "smtp.gmail.com");
		props.setProperty("mail.smtp.port", "587");
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.smtp.starttls.enable", "true");

		Authenticator auth = new SMTPAuthenticator(email, password);
		Session session = Session.getInstance(props, auth);
		MimeMessage msg = new MimeMessage(session);

		try {
			msg.setText(html, "UTF-8", "html");
			msg.setSubject(subject);
			msg.setFrom(new InternetAddress(email));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			Transport.send(msg);
		} catch (MessagingException e) {
			log.error("Email delivery failed: " + e.getMessage());
		}
	}
	
	@Async
	@Override
	public void sendResetPasswordEmail(String emailTo, String secret, HttpServletRequest request) {
		String to = emailTo;
		String subject = "Social Demand - Reset Password Link";
		String html = "Please click on the following link to reset your password, \n\n<a href='" + protocol + "://" + hostname + ":" + port + "/api/auth/reset/verification?email=" + emailTo + "&token=" + secret + "'>RESET PASSWORD LINK!</a>";

		Properties props = new Properties();
		props.setProperty("mail.host", "smtp.gmail.com");
		props.setProperty("mail.smtp.port", "587");
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.smtp.starttls.enable", "true");

		Authenticator auth = new SMTPAuthenticator(email, password);
		Session session = Session.getInstance(props, auth);
		MimeMessage msg = new MimeMessage(session);

		try {
			msg.setText(html, "UTF-8", "html");
			msg.setSubject(subject);
			msg.setFrom(new InternetAddress(email));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			Transport.send(msg);
		} catch (MessagingException e) {
			log.error("Email delivery failed: " + e.getMessage());
		}
	}

	private class SMTPAuthenticator extends Authenticator {
		private PasswordAuthentication authentication;

		public SMTPAuthenticator(String login, String password) {
			authentication = new PasswordAuthentication(login, password);
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return authentication;
		}
	}
}
