package jorge.rv.quizzz.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jorge.rv.quizzz.model.MailRegistrationToken;

@Repository
public interface MailRegistrationTokenRepository extends TokenRepository<MailRegistrationToken> {
	@Modifying
	@Query("delete from MailRegistrationToken t where t.expirationDate <= ?1")
	void deletePreviousTo(Date date);
}
