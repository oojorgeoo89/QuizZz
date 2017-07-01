package jorge.rv.quizzz.service.usermanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jorge.rv.quizzz.model.ForgotPasswordToken;
import jorge.rv.quizzz.repository.ForgotPasswordTokenRepository;
import jorge.rv.quizzz.service.usermanagement.utils.TokenGenerator;

@Service
public class TokenServiceForgotPassword extends TokenServiceAbs<ForgotPasswordToken> {

	@Autowired
	public TokenServiceForgotPassword(ForgotPasswordTokenRepository forgotPasswordTokenRepository, TokenGenerator tokenGenerator) {
		super(tokenGenerator, forgotPasswordTokenRepository);
	}

	@Override
	protected ForgotPasswordToken create() {
		return new ForgotPasswordToken();
	}

}
