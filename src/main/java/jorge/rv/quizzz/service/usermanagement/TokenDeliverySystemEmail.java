package jorge.rv.quizzz.service.usermanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jorge.rv.quizzz.model.TokenModel;
import jorge.rv.quizzz.model.TokenType;
import jorge.rv.quizzz.model.User;

@Service
public class TokenDeliverySystemEmail implements TokenDeliverySystem {
	
	private static final String BASE_CONFIG_URI = "quizzz.tokens.%s";
	
	private Environment env;
	private JavaMailSender mailSender;
	
	@Autowired
	public TokenDeliverySystemEmail(Environment env, JavaMailSender mailSender) {
		this.env = env;
		this.mailSender = mailSender;
	}

	@Override
	public void sendTokenToUser(TokenModel token, User user, TokenType tokenType) {
		String base_config = String.format(BASE_CONFIG_URI, tokenType.toString().toLowerCase());
		String url = String.format(env.getProperty(base_config + ".url"), user.getId(), token.getToken());
	
		try {
			sendByMail(user, url, base_config);
		} catch (Exception e) {
        	// This runs on a thread so it is too late to notify the user. A re-try mechanism could be put in place.
        	e.printStackTrace();
        }
	}

	private void sendByMail(User user, String url, String base_config) {
		String subject = env.getProperty(base_config + ".subject");
		String body = String.format(env.getProperty(base_config + ".body"), user.getUsername(), url);
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("noreply@quizzz.com");
        mailMessage.setSubject(subject);
        mailMessage.setText(body);    
        
        mailSender.send(mailMessage);
	}
}
