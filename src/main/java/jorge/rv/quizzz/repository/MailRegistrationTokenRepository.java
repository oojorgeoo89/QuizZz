package jorge.rv.quizzz.repository;

import org.springframework.stereotype.Repository;

import jorge.rv.quizzz.model.MailRegistrationToken;

@Repository
public interface MailRegistrationTokenRepository extends TokenRepository<MailRegistrationToken> {

}
