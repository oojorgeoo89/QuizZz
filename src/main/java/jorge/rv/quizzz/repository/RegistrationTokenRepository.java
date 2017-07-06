package jorge.rv.quizzz.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jorge.rv.quizzz.model.RegistrationToken;

@Repository
public interface RegistrationTokenRepository extends TokenRepository<RegistrationToken> {
	@Modifying
	@Query("delete from RegistrationToken t where t.expirationDate <= ?1")
	void deletePreviousTo(Date date);
}
