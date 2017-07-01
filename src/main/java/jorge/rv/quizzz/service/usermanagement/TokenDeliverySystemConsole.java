package jorge.rv.quizzz.service.usermanagement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import jorge.rv.quizzz.model.TokenModel;
import jorge.rv.quizzz.model.TokenType;
import jorge.rv.quizzz.model.User;
import jorge.rv.quizzz.service.AccessControlServiceImpl;

@Service
public class TokenDeliverySystemConsole implements TokenDeliverySystem {

	private static final Logger logger = LoggerFactory.getLogger(AccessControlServiceImpl.class);
	private static final String CONFIG_URI = "quizzz.tokens.urls.";
	
	private Environment env;
	
	@Autowired
	public TokenDeliverySystemConsole(Environment env) {
		this.env = env;
	}

	@Override
	public void sendTokenToUser(TokenModel token, User user, TokenType tokenType) {
		String url = String.format(env.getProperty(CONFIG_URI + tokenType.toString().toLowerCase()), user.getId(), token.getToken());
		logger.info(url);
	}

}
