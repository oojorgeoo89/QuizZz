package jorge.rv.quizzz.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import jorge.rv.quizzz.model.TokenModel;

@NoRepositoryBean
public interface TokenRepository<T extends TokenModel> extends CrudRepository<T, Long> {
	T findByToken(String token);
}