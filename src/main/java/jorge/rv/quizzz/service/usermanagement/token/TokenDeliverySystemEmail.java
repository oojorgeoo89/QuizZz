package jorge.rv.quizzz.service.usermanagement.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jorge.rv.quizzz.model.TokenModel;
import jorge.rv.quizzz.model.TokenType;
import jorge.rv.quizzz.model.User;

@Service
public class TokenDeliverySystemEmail implements TokenDeliverySystem {

	private static final String BASE_CONFIG_URI = "quizzz.tokens.%s";

	private MessageSource messageSource;
	private JavaMailSender mailSender;

	@Autowired
	public TokenDeliverySystemEmail(MessageSource messageSource, JavaMailSender mailSender) {
		this.messageSource = messageSource;
		this.mailSender = mailSender;
	}

	@Override
	public void sendTokenToUser(TokenModel token, User user, TokenType tokenType) {
		String base_config = String.format(BASE_CONFIG_URI, tokenType.toString().toLowerCase());
		String url = String.format(messageSource.getMessage(base_config + ".url", null, null), user.getId(),
				token.getToken());

		try {
			sendByMail(user, url, base_config);
		} catch (Exception e) {
			// This runs on a thread so it is too late to notify the user. A
			// re-try mechanism could be put in place.
			e.printStackTrace();
		}
	}

	private void sendByMail(User user, String url, String base_config) {
		String subject = messageSource.getMessage(base_config + ".subject", null, null);
		String body = String.format(messageSource.getMessage(base_config + ".body", null, null), user.getUsername(),
				url);

		SimpleMailMessage mailMessage = new SimpleMailMessage();

		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("noreply@quizzz.com");
		mailMessage.setSubject(subject);
		mailMessage.setText(body);

		mailSender.send(mailMessage);
	}
}
