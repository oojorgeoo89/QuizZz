package jorge.rv.quizzz.service.usermanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jorge.rv.quizzz.model.MailRegistrationToken;
import jorge.rv.quizzz.repository.MailRegistrationTokenRepository;
import jorge.rv.quizzz.service.usermanagement.utils.TokenGenerator;

@Service
public class TokenServiceMailRegistration extends TokenServiceAbs<MailRegistrationToken> {

	@Autowired
	public TokenServiceMailRegistration(MailRegistrationTokenRepository mailTokenRepository, TokenGenerator tokenGenerator) {
		super(tokenGenerator, mailTokenRepository);
	}

	@Override
	protected MailRegistrationToken create() {
		return new MailRegistrationToken();
	}

}
