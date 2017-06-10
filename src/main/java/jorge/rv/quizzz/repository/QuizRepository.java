package jorge.rv.quizzz.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import jorge.rv.quizzz.model.Quiz;

@Repository("QuizRepository")
public interface QuizRepository extends PagingAndSortingRepository<Quiz, Long> {
	
}
