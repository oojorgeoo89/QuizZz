package jorge.rv.quizzz.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import jorge.rv.quizzz.model.Quiz;

@Repository("QuizRepository")
public interface QuizRepository extends PagingAndSortingRepository<Quiz, Long> {

	@Query("select q from Quiz q where q.name like %?1%")
	Page<Quiz> searchByName(String name, Pageable pageable);
}
