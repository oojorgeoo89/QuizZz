package jorge.rv.quizzz.service.usermanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jorge.rv.quizzz.model.MailRegistrationToken;
import jorge.rv.quizzz.repository.MailRegistrationTokenRepository;
import jorge.rv.quizzz.service.usermanagement.utils.TokenGenerator;

@Service
public class TokenServiceMailRegistration extends TokenServiceAbs<MailRegistrationToken> {
	
	@Value("${quizzz.tokens.registration_mail.timeout}")
	private Integer expirationTimeInMinutes = 86400;

	@Autowired
	public TokenServiceMailRegistration(MailRegistrationTokenRepository mailTokenRepository, TokenGenerator tokenGenerator) {
		super(tokenGenerator, mailTokenRepository);
	}

	@Override
	protected MailRegistrationToken create() {
		return new MailRegistrationToken();
	}

	@Override
	protected int getExpirationTimeInMinutes() {
		return expirationTimeInMinutes;
	}

	public void setExpirationTimeInMinutes(Integer expirationTimeInMinutes) {
		this.expirationTimeInMinutes = expirationTimeInMinutes;
	}
}
