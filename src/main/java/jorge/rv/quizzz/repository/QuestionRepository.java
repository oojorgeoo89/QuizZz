package jorge.rv.quizzz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jorge.rv.quizzz.model.Question;

@Repository("QuestionRepository")
public interface QuestionRepository extends JpaRepository<Question, Long>{
	
}
