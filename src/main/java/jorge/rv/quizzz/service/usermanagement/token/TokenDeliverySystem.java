package jorge.rv.quizzz.service.usermanagement.token;

import org.springframework.scheduling.annotation.Async;

import jorge.rv.quizzz.model.TokenModel;
import jorge.rv.quizzz.model.TokenType;
import jorge.rv.quizzz.model.User;

public interface TokenDeliverySystem {
	@Async
	void sendTokenToUser(TokenModel token, User user, TokenType tokenType);
}
