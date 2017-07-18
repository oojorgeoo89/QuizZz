package jorge.rv.quizzz.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import jorge.rv.quizzz.model.TokenModel;

@NoRepositoryBean
public interface TokenRepository<T extends TokenModel> extends CrudRepository<T, Long> {
	T findByToken(String token);

	@Modifying
	void deletePreviousTo(Date date);
}