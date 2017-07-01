package jorge.rv.quizzz.repository;

import org.springframework.stereotype.Repository;

import jorge.rv.quizzz.model.ForgotPasswordToken;

@Repository
public interface ForgotPasswordTokenRepository extends TokenRepository<ForgotPasswordToken>{
	
}
